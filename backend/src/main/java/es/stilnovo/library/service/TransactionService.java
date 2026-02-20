package es.stilnovo.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.Transaction;
import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.ProductRepository;
import es.stilnovo.library.repository.TransactionRepository;
import es.stilnovo.library.repository.UserRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    /**
     * Executes the business logic for a purchase.
     * Updates product status and creates a permanent transaction record.
     */
    @Transactional
    public Transaction executePurchase(Product product, User buyer) {
        
        // 1. Business Rule: Product must be 'active'
        if (!"active".equalsIgnoreCase(product.getStatus())) {
            throw new IllegalStateException("Product is no longer available for sale.");
        }

        // 2. Create and configure the Transaction object
        Transaction transaction = new Transaction(
            product.getSeller(),
            buyer,
            product,
            "Completed"
        );

        User seller = product.getSeller();
        Double productPrice = product.getPrice();
        Double sellerBalance = seller.getBalance();
        Double sellerTotalRevenue = seller.getTotalRevenue();

        sellerBalance += productPrice;
        sellerTotalRevenue += productPrice;
        
        seller.setBalance(sellerBalance);
        seller.setTotalRevenue(sellerTotalRevenue);

        userRepository.save(seller);

        // 3. Mark the product as 'Sold' in the database
        product.setStatus("Sold");
        productRepository.save(product);

        // 4. Save the transaction and return it
        return transactionRepository.save(transaction);
    }

    /**
     * Gets a transaction if the current user is either the buyer or seller.
     * Used for Invoice generation (both parties can see it).
     * @param transactionId The ID of the transaction
     * @param username The authenticated username from Principal
     * @return The transaction if user has access
     * @throws IllegalStateException if user is not involved or transaction not found
     */
    public Transaction getTransactionForInvolvedUser(long transactionId, String username) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        
        // Security check: User must be either buyer or seller
        boolean isBuyer = transaction.getBuyer().getName().equals(username);
        boolean isSeller = transaction.getSeller().getName().equals(username);
        
        if (!isBuyer && !isSeller) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        
        return transaction;
    }

    /**
     * Gets a transaction if the current user is the seller.
     * Used for Shipping Label generation (only seller can generate it).
     * @param transactionId The ID of the transaction
     * @param username The authenticated username from Principal
     * @return The transaction if user is the seller
     * @throws IllegalStateException if user is not the seller or transaction not found
     */
    public Transaction getTransactionForSeller(long transactionId, String username) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        
        // Security check: User must be the seller
        if (!transaction.getSeller().getName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        
        return transaction;
    }

    /**
     * Gets all transactions where the user is the seller.
     * Used for statistics and reports (secure via Principal-based username).
     * @param username The authenticated username from Principal
     * @return List of transactions where this user is the seller
     */
    public java.util.List<Transaction> getSellerTransactions(String username) {
        User seller = userRepository.findByName(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        return transactionRepository.findBySeller(seller);
    }
}
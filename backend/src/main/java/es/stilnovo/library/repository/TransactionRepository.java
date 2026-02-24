package es.stilnovo.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.stilnovo.library.model.Transaction;
import es.stilnovo.library.model.User;
import java.util.List;

/**
 * Repository for Transaction entities.
 * Provides methods to retrieve sales and purchases for specific users.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Retrieves all transactions where the user was the buyer
    List<Transaction> findByBuyerUserId(Long buyerId);
    
    // Retrieves all transactions where the user was the seller
    List<Transaction> findBySellerUserId(Long sellerId);
    
    // Retrieves all transactions where the user entity is the seller
    List<Transaction> findBySeller(User seller);
    
    // Find all transactions where this user is buyer OR seller
    List<Transaction> findByBuyerOrSeller(User buyer, User seller);
}
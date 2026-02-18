package es.stilnovo.library.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.ProductRepository;
import es.stilnovo.library.service.MailService;
import es.stilnovo.library.service.TransactionService;
import es.stilnovo.library.service.UserService;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired  
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * Finalizes the purchase of a product.
     * It validates that the buyer is not the seller and then delegates the 
     * financial and inventory logic to the service layer.
     * * @param productId The ID of the item being purchased.
     * @param principal The authenticated buyer from the Security Context.
     * @return A redirect to the orders page on success, or back to the product on error.
     */
    @PostMapping("/transactions/confirm/{productId}")
    public String confirmPayment(@PathVariable long productId, Principal principal) {
        
        // 1. Context Retrieval: Fetch full entities
        User buyer = userService.getFullUserProfile(principal.getName());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // 2. Security Check: Prevent self-buying
        if (product.getSeller().getUserId().equals(buyer.getUserId())) {
            return "redirect:/info-product-page/" + productId + "?error=self_purchase";
        }

        try {
            // 3. Execution: Delegate database updates to the Service Layer
            transactionService.executePurchase(product, buyer);

            // 4. Notification: Send the confirmation email
            String body = "<h1>Purchase Successful!</h1><p>You have bought: " + product.getName() + "</p>";
            mailService.sendHtml(buyer.getEmail(), "Stilnovo: Purchase Confirmation", body);

            // 5. Clean Redirect: No IDs in the URL for the user's dashboard
            return "redirect:/sales-and-orders-page";

        } catch (IllegalStateException e) {
            // Handle business logic errors (e.g., product already sold or insufficient funds)
            return "redirect:/info-product-page/" + productId + "?error=" + e.getMessage();
        }
    }
}
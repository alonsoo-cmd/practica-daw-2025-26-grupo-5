package es.stilnovo.library.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.stilnovo.library.model.Inquiry;
import es.stilnovo.library.repository.InquiryRepository;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    /**
     * Gets the most recent inquiry from a specific buyer for a specific product.
     * Used for cooldown validation to prevent spam.
     * @param buyerId The ID of the buyer
     * @param productId The ID of the product
     * @return Optional containing the last inquiry, or empty if none found
     */
    public Optional<Inquiry> getLastInquiry(Long buyerId, Long productId) {
        Inquiry lastInquiry = inquiryRepository
                .findTopByBuyerIdAndProductIdOrderByCreatedAtDesc(buyerId, productId);
        return Optional.ofNullable(lastInquiry);
    }

    /**
     * Saves a new inquiry to the database.
     * @param inquiry The inquiry to save
     * @return The saved inquiry with generated ID
     */
    public Inquiry saveInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    /**
     * Creates and saves a new inquiry with the given parameters.
     * @param productId The product ID
     * @param productName The product name
     * @param sellerId The seller's user ID
     * @param sellerEmail The seller's email
     * @param buyerId The buyer's user ID
     * @param buyerName The buyer's name
     * @param buyerEmail The buyer's email
     * @param buyerPhone The buyer's phone (optional)
     * @param inquiryType The type of inquiry
     * @param message The inquiry message
     * @param status The status (e.g., "SENT", "FAILED_MAIL")
     * @return The saved inquiry
     */
    public Inquiry createInquiry(Long productId, String productName, Long sellerId, 
                                    String sellerEmail, Long buyerId, String buyerName, 
                                    String buyerEmail, String buyerPhone, String inquiryType, 
                                    String message, String status) {
        Inquiry inquiry = new Inquiry();
        inquiry.setProductId(productId);
        inquiry.setProductName(productName);
        inquiry.setSellerId(sellerId);
        inquiry.setSellerEmail(sellerEmail);
        inquiry.setBuyerId(buyerId);
        inquiry.setBuyerName(buyerName);
        inquiry.setBuyerEmail(buyerEmail);
        inquiry.setBuyerPhone(buyerPhone);
        inquiry.setInquiryType(inquiryType);
        inquiry.setMessage(message);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setStatus(status);
        
        return inquiryRepository.save(inquiry);
    }
}

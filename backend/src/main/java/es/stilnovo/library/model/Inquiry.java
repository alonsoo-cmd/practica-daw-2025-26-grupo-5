package es.stilnovo.library.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a customer inquiry/question about a product.
 * Allows buyers to contact sellers before purchase.
 */
@Entity(name = "InquiryTable")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** ID of the product this inquiry is about */
    private Long productId;
    
    /** Name of the product */
    private String productName;
    
    /** ID of the seller */
    private Long sellerId;
    
    /** Email of the seller */
    private String sellerEmail;
    
    /** ID of the buyer asking the question */
    private Long buyerId;
    
    /** Name of the buyer */
    private String buyerName;
    
    /** Email of the buyer */
    private String buyerEmail;
    
    /** Phone number of the buyer */
    private String buyerPhone;
    
    /** Type of inquiry (question, offer, complaint, etc.) */
    private String inquiryType;

    @Column(columnDefinition = "TEXT")
    private String message;

    /** When the inquiry was created */
    private LocalDateTime createdAt;
    
    /** Status of inquiry: Open, Answered, Closed, etc. */
    private String status;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(String inquiryType) {
        this.inquiryType = inquiryType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

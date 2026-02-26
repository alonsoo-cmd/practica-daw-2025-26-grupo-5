package es.stilnovo.library.model;

import java.sql.Blob;

import com.fasterxml.jackson.annotation.JsonIgnore; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

/**
 * Image: Stores product images as binary data
 * 
 * This entity manages:
 * - Binary image files (BLOB format)
 * - Image-to-product association
 * - Image metadata (ID only)
 * 
 * Relationships:
 * - ManyToOne: Product (the product this image belongs to)
 * 
 * JsonIgnore prevents infinite JSON serialization loops
 * Used by: ImageService, ProductService
 */
@Entity(name="ImageTable")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The image file stored as binary data */
    @Lob
    private Blob imageFile;

    /** The product this image belongs to */
    @ManyToOne
    @JsonIgnore
    private Product product;

    public Image() {
    }

    public Image(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
    @Override
    public String toString() {
        return "Image [id=" + id + "]";
    }
}
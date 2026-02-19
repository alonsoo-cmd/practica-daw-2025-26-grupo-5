package es.stilnovo.library.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity(name = "UserTable")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    private String name;

    private String encodedPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Lob
    private Blob profileImage;

    private Double rating;

    @Column(unique = true, nullable = false)
    private String email;

    // banned status for users
    @Column(nullable = false)
    private boolean banned = false;

    // Payment / card info (some controllers/services referenced these)
    private String cardNumber;
    private String cardCvv;
    private String cardExpiringDate;

    // Additional metadata expected by services
    private String description;

    // number of ratings / reviews
    private int numRatings;

    // Financial fields used by TransactionService / others
    private double balance;
    private double totalRevenue;

    // One-to-many: products the user is selling
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    // Favorite products (many-to-many)
    @ManyToMany
    private List<Product> favoriteProducts = new ArrayList<>();

    // Valorations / ratings received as seller (class Valoration assumed present in project)
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoration> valorations = new ArrayList<>();

    /* ----------------- CONSTRUCTORS ----------------- */

    public User() {}

    /**
     * Simple constructor used by DataBaseInitializer and many places.
     * Accepts varargs roles.
     */
    public User(String name, String encodedPassword, String email, Blob profileImage, Double rating, String... roles) {
        this.name = name;
        this.encodedPassword = encodedPassword;
        this.email = email;
        this.profileImage = profileImage;
        this.rating = rating;
        this.roles = (roles != null) ? List.of(roles) : new ArrayList<>();
    }

    /**
     * Full constructor to be compatible with older controller usages (signup code that passed many params).
     * Order matches the example that caused the constructor error in your logs.
     *
     * Example call that motivated this signature:
     * new User(username, encodedPassword, email, imageBlob, 5.0, null, null, null , 0, 0.0, 0.0, null, "ROLE_USER");
     *
     * Parameters:
     *  - cardNumber, cardCvv, cardExpiringDate, numRatings, balance, totalRevenue, description, roles...
     */
    public User(String name,
                String encodedPassword,
                String email,
                Blob profileImage,
                Double rating,
                String cardNumber,
                String cardCvv,
                String cardExpiringDate,
                int numRatings,
                double balance,
                double totalRevenue,
                String description,
                String... roles) {

        this.name = name;
        this.encodedPassword = encodedPassword;
        this.email = email;
        this.profileImage = profileImage;
        this.rating = rating;
        this.cardNumber = cardNumber;
        this.cardCvv = cardCvv;
        this.cardExpiringDate = cardExpiringDate;
        this.numRatings = numRatings;
        this.balance = balance;
        this.totalRevenue = totalRevenue;
        this.description = description;
        this.roles = (roles != null) ? List.of(roles) : new ArrayList<>();
    }

    /* ----------------- BASIC GETTERS / SETTERS ----------------- */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	// NOTE: Some code calls getEncodedPassword() / setEncodedPassword()
    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Blob getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Blob profileImage) {
        this.profileImage = profileImage;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /* ----------------- BANNED ----------------- */

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    /* ----------------- CARD / FINANCIAL ----------------- */

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    // some controllers used setCardnumber (lowercase n) — provide alias for compatibility
    public void setCardnumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public String getCardExpiringDate() {
        return cardExpiringDate;
    }

    public void setCardExpiringDate(String cardExpiringDate) {
        this.cardExpiringDate = cardExpiringDate;
    }

    public String getDescription() {
        return description;
    }

    public void setUserDescription(String description) {
        this.description = description;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    // alias used in some code: setNumratings (lowercase r)
    public void setNumratings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    /* ----------------- PRODUCTS / FAVORITES / VALORATIONS ----------------- */

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Convenience method to add a product to the user's products list.
     * The Product entity should set its seller to this user elsewhere (service).
     */
    public void addProduct(Product product) {
        if (!this.products.contains(product)) {
            this.products.add(product);
        }
    }

    public List<Product> getFavoriteProducts() {
        return favoriteProducts;
    }

    public void setFavoriteProducts(List<Product> favoriteProducts) {
        this.favoriteProducts = favoriteProducts;
    }

    public void addFavorite(Product product) {
        if (!this.favoriteProducts.contains(product)) {
            this.favoriteProducts.add(product);
        }
    }

    public List<Valoration> getValorations() {
        return valorations;
    }

    public void setValorations(List<Valoration> valorations) {
        this.valorations = valorations;
    }

    public void addValoration(Valoration v) {
        if (!this.valorations.contains(v)) {
            this.valorations.add(v);
        }
    }

    /* ----------------- UTILITY ----------------- */

    public boolean isAdmin() {
        return roles != null && roles.contains("ROLE_ADMIN");
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", banned=" + banned +
                '}';
    }
}


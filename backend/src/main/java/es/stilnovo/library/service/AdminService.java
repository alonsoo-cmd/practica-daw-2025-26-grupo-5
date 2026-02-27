package es.stilnovo.library.service;

import java.io.IOException;

import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.ProductRepository;
import es.stilnovo.library.repository.UserRepository;
import java.util.List;

import es.stilnovo.library.model.Image;



/**
 * AdminService: Manages administrative operations
 * 
 * This service handles:
 * - User deletion and account removal
 * - User banning/unbanning
 * - System statistics (total users, banned users count)
 * - Admin panel data preparation
 * 
 * Uses: UserRepository, UserService
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService; 

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageService imageService;

    @Transactional
    public void deleteUser(Long userId) {
        // Delegate all responsability to userService
        userService.deleteUserById(userId);
    }

    @Transactional(readOnly = true)
    public int getNumBanneds() {
        return userRepository.countByBanned(true);
    }

    @Transactional(readOnly = true)
    public int getNumTotalUsers(){
        return (int) userRepository.count();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public void updateUserAsAdmin(Long id,
                                MultipartFile newProfilePhoto,
                                String email,
                                String cardNumber,
                                String cardCvv,
                                String cardExpiringDate,
                                String description) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (newProfilePhoto != null && !newProfilePhoto.isEmpty()) {
            user.setProfileImage(BlobProxy.generateProxy(
                    newProfilePhoto.getInputStream(),
                    newProfilePhoto.getSize()
            ));
        }

        if (email != null && !email.trim().isEmpty()) user.setEmail(email);
        if (cardNumber != null && !cardNumber.trim().isEmpty()) user.setCardNumber(cardNumber);
        if (cardCvv != null && !cardCvv.trim().isEmpty()) user.setCardCvv(cardCvv);
        if (cardExpiringDate != null && !cardExpiringDate.trim().isEmpty()) user.setCardExpiringDate(cardExpiringDate);
        if (description != null && !description.trim().isEmpty()) user.setUserDescription(description);

        userRepository.save(user);
    }

    @Transactional
    public void updateProductAsAdmin(long id,
                                    Product updatedData,
                                    MultipartFile imageFile) throws IOException {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Sin verificación de propietario (admin puede todo)

        existingProduct.setName(updatedData.getName());
        existingProduct.setPrice(updatedData.getPrice());
        existingProduct.setDescription(updatedData.getDescription());
        existingProduct.setCategory(updatedData.getCategory());
        existingProduct.setLocation(updatedData.getLocation());
        existingProduct.setStatus(updatedData.getStatus());

        if (imageFile != null && !imageFile.isEmpty()) {

            Image newImage = imageService.createImage(imageFile.getInputStream());

            existingProduct.setImage(newImage);
            newImage.setProduct(existingProduct);
        }

        productRepository.save(existingProduct);
    }

}


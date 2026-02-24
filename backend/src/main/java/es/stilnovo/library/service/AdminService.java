package es.stilnovo.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.stilnovo.library.model.User;
import es.stilnovo.library.repository.ProductRepository;
import es.stilnovo.library.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        // 1. Delete user's products
        productRepository.deleteAll(productRepository.findBySeller(user));

        // 2. Clear favorites
        user.getFavoriteProducts().clear();

        // 3. Delete user
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public int getNumBanneds() {
        return userRepository.countByBanned(true);
    }

    @Transactional(readOnly = true)
    public int getNumTotalUsers(){
        return (int) userRepository.count();
    }

}


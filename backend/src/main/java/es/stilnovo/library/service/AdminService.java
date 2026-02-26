package es.stilnovo.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.stilnovo.library.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for admin operations (user management, statistics) */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService; 

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

}


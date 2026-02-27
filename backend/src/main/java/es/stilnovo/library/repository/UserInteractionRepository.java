package es.stilnovo.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.stilnovo.library.model.User;
import es.stilnovo.library.model.UserInteraction;

/**
 * Repository for user interaction tracking (views, likes, purchases)
 */
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    void deleteByUser(User user);
    void deleteByProductSeller(User seller);

    List<UserInteraction> findByProductSeller(User seller);
}
package es.stilnovo.library.repository;

import es.stilnovo.library.model.User;
import es.stilnovo.library.model.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for user interaction tracking (views, likes, purchases)
 */
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    void deleteByUser(User user);
    void deleteByProductSeller(User seller);
}
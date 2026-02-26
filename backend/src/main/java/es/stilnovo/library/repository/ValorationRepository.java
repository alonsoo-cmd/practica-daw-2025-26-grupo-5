package es.stilnovo.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.stilnovo.library.model.Valoration;
import es.stilnovo.library.model.Transaction;
import es.stilnovo.library.model.User;
import java.util.List;

/**
 * Repository interface for Valoration entity.
 * Provides abstracted data access for user feedback and reputation management.
 */
@Repository
/**
 * Repository for Valoration (review/rating) CRUD operations
 */
public interface ValorationRepository extends JpaRepository<Valoration, Long> {

    /**
     * Checks if a valoration already exists for a specific transaction.
     * This is used to prevent duplicate reviews for the same purchase.
     * * @param transaction The completed purchase to check.
     * @return true if the transaction has already been rated, false otherwise.
     */
    boolean existsByTransaction(Transaction transaction);

    /**
     * Retrieves all valorations received by a specific seller.
     * Used to calculate the average reputation score.
     * * @param seller The user who received the ratings.
     * @return A list of valorations for the seller.
     */
    List<Valoration> findBySeller(User seller);

    /**
     * Retrieves all valorations submitted by a specific buyer.
     * Used to display the user's review history.
     * * @param buyer The user who wrote the reviews.
     * @return A list of valorations authored by the buyer.
     */
    List<Valoration> findByBuyer(User buyer);

    // Deletes all valorations associated with a list of transactions in one go
    @Modifying
    @Query("DELETE FROM Valoration v WHERE v.transaction IN :transactions")
    void deleteByTransactionIn(List<Transaction> transactions);
}
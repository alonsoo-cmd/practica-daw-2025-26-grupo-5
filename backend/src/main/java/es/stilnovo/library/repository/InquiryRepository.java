package es.stilnovo.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.stilnovo.library.model.Inquiry;
import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    /** Find the most recent inquiry from a buyer about a product */
    Inquiry findTopByBuyerAndProductOrderByCreatedAtDesc(User buyer, Product product);
}

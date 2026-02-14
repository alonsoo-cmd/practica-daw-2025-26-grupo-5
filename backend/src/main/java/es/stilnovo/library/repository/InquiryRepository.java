package es.stilnovo.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.stilnovo.library.model.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Inquiry findTopByBuyerIdAndProductIdOrderByCreatedAtDesc(Long buyerId, Long productId);
}

package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findBySellerId(String sellerId);

    default Optional<Review> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<Review> reviews) {
        saveAll(reviews);
    }
}
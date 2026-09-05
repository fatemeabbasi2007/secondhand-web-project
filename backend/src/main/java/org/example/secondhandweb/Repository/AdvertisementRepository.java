package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.myEnum.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {

    List<Advertisement> findByStatus(AdStatus status);

    @Query("""
        SELECT a
        FROM Advertisement a
        WHERE a.status = :status
          AND (:keyword IS NULL
               OR a.title LIKE CONCAT('%', :keyword, '%')
               OR a.description LIKE CONCAT('%', :keyword, '%'))
          AND (:categoryId IS NULL OR a.categoryId = :categoryId)
          AND (:city IS NULL OR a.city = :city)
          AND (:minPrice IS NULL OR a.price >= :minPrice)
          AND (:maxPrice IS NULL OR a.price <= :maxPrice)
        """)
    List<Advertisement> searchActiveAds(
            @Param("status") AdStatus status,
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("city") String city,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );


    default Optional<Advertisement> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<Advertisement> advertisements) {
        saveAll(advertisements);
    }
}
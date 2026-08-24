package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.myEnum.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {

    List<Advertisement> findByStatus(AdStatus status);

    default Optional<Advertisement> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<Advertisement> advertisements) {
        saveAll(advertisements);
    }
}
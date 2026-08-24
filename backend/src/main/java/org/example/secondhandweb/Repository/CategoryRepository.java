package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    default Optional<Category> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<Category> categories) {
        saveAll(categories);
    }
}
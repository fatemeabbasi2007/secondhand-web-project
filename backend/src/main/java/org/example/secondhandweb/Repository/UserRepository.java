package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    default Optional<User> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<User> users) {
        saveAll(users);
    }
}
package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByIdIn(Collection<String> ids);
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNum(String phoneNum);


    default Optional<User> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<User> users) {
        saveAll(users);
    }
}
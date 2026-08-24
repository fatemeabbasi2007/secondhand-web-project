package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    @Query("SELECT c FROM Conversation c WHERE c.buyerId = :userId OR c.sellerId = :userId")
    List<Conversation> findConversationsByUserId(@Param("userId") String userId);

    default Optional<Conversation> findByID(String id) {
        return findById(id);
    }

    default void saveALL(List<Conversation> conversations) {
        saveAll(conversations);
    }
}
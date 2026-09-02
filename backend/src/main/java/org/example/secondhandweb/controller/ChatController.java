package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.exeption.*;
//import org.example.secondhandweb.model.ConversationPreviewDTO;
//import org.example.secondhandweb.model.Message;
//import org.example.secondhandweb.model.User;
//import org.example.secondhandweb.service.ChatService;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.dto.ConversationPreviewDTO;
import org.example.secondhandweb.exception.*;
import org.example.secondhandweb.model.Message;
import org.example.secondhandweb.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    private User getAuthenticatedUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ForbiddenException.NoAccessException("ابتدا وارد سامانه شوید");
        }
        return user;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam String advertisementId,
            @RequestBody Message message ,
            HttpSession session) {
        User loggedInUser = getAuthenticatedUser(session);
        chatService.startOrSendMessage(advertisementId, loggedInUser.getId(), message);
        return ResponseEntity.ok(new MessageResponse("پیام فرستاده شد"));
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getUserInbox(HttpSession session) {
        User loggedInUser = getAuthenticatedUser(session);
        List<ConversationPreviewDTO> inbox = chatService.getUserConversations(loggedInUser.getId());
        return ResponseEntity.ok(inbox);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<?> getChatHistory(@PathVariable String conversationId, HttpSession session) {
        User user = getAuthenticatedUser(session);
        List<Message> messages = chatService.getMessagesInConversation(conversationId, user.getId());
        return ResponseEntity.ok(messages);

    }
}
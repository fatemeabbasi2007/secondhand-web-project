package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.exeption.*;
//import org.example.secondhandweb.model.ConversationPreviewDTO;
//import org.example.secondhandweb.model.Message;
//import org.example.secondhandweb.model.User;
//import org.example.secondhandweb.service.ChatService;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.dto.ConversationPreviewDTO;
import org.example.secondhandweb.exeption.*;
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


    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam String advertisementId,
            @RequestBody Message message ,
            HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد سامانه شوید"));
        }
        try{
            chatService.startOrSendMessage(advertisementId , loggedInUser.getId(), message);
            return ResponseEntity.ok(new MessageResponse("پیام فرستاده شد"));
        }catch (InvalidMessageException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (AdvertisementNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (SameSellerAndBuyerIdExcpetion | UserBannedException | NoAcceessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getUserInbox(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد سامانه شوید"));
        }
        try{
            List<ConversationPreviewDTO> inbox = chatService.getUserConversations(loggedInUser.getId());
            return ResponseEntity.ok(inbox);        }catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (UserBannedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<?> getChatHistory(@PathVariable String conversationId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }
        try{
            List<Message> messages = chatService.getMessagesInConversation(conversationId , user.getId());
            return ResponseEntity.ok(messages);
        }catch (NoAcceessException|UserBannedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }catch (UserNotFoundException| ConversationNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }

    }
}
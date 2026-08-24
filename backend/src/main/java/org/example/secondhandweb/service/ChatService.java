package org.example.secondhandweb.service;

import org.example.secondhandweb.Repository.AdvertisementRepository;
import org.example.secondhandweb.Repository.ConversationRepository;
import org.example.secondhandweb.exeption.*;
import org.example.secondhandweb.model.*;
//import org.example.secondhandweb.repository.AdvertisementRepository;
//import org.example.secondhandweb.repository.ConversationRepository;
//import org.example.secondhandweb.repository.UserRepository;
import org.example.secondhandweb.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.secondhandweb.dto.ConversationPreviewDTO; // 🟢 New package

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    // فیلدهای ریپازیتوری در آینده اینجا اینجکت می‌شوند:
     private final ConversationRepository conversationRepository;
     private final UserRepository userRepository;
     private final AdvertisementRepository advertisementRepository;


    public ChatService(ConversationRepository conversationRepository , UserRepository userRepository, AdvertisementRepository advertisementRepository ) {
        this.conversationRepository =  conversationRepository;
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public Conversation startOrSendMessage(String advertisementId, String loggedInUserId, Message message) {

        if (message == null || message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new InvalidMessageException("متن پیام نمی‌تواند خالی باشد");
        }

        Advertisement ad = advertisementRepository.findByID(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("آگهی مورد نظر یافت نشد"));

        String sellerId = ad.getOwnerId();
        String buyerId;

        // اگر کاربر جاری فروشنده است
        if (loggedInUserId.equals(sellerId)) {
            if (message.getSenderId() != null && !message.getSenderId().isBlank()) {
                buyerId = message.getSenderId();
            } else {
                // پیدا کردن چت موجود برای یافتن buyerId
                List<Conversation> convs = conversationRepository.findConversationsByUserId(loggedInUserId);
                buyerId = convs.stream()
                        .filter(c -> advertisementId.equals(c.getAdvertisementId()))
                        .map(Conversation::getBuyerId)
                        .findFirst()
                        .orElseThrow(() -> new InvalidMessageException("گفت‌وگویی یافت نشد. خریدار باید ابتدا پیام دهد."));
            }
        } else {
            buyerId = loggedInUserId;
        }

        if (buyerId.equals(sellerId)) {
            throw new SameSellerAndBuyerIdExcpetion("کاربر نمی‌تواند به آگهی خودش پیام دهد");
        }

        User sender = userRepository.findByID(loggedInUserId)
                .orElseThrow(() -> new UserNotFoundException("حساب شما یافت نشد"));
        User receiver = userRepository.findByID(loggedInUserId.equals(buyerId) ? sellerId : buyerId)
                .orElseThrow(() -> new UserNotFoundException("مخاطب یافت نشد"));

        if (!sender.isEnabled() || !receiver.isEnabled()) {
            throw new UserBannedException("یکی از طرفین مسدود است و امکان ارسال پیام وجود ندارد");
        }

        String conversationId = buyerId + "_" + advertisementId;
        Conversation conversation = conversationRepository.findByID(conversationId).orElseGet(() -> {
            if (loggedInUserId.equals(sellerId)) {
                throw new InvalidMessageException("گفت‌وگویی یافت نشد. خریدار باید ابتدا پیام دهد.");
            }
            Conversation newConv = new Conversation();
            newConv.setId(conversationId);
            newConv.setAdvertisementId(advertisementId);
            newConv.setBuyerId(buyerId);
            newConv.setSellerId(sellerId);
            return newConv;
        });

        message.setSenderId(loggedInUserId);
        message.setSentAt(LocalDateTime.now());

        conversation.addMessageToList(message);
        conversation.setLastMessageAt(message.getSentAt());
        conversation.setLastMessagePreview(message.getContent());
        conversationRepository.save(conversation);

        return conversation;
    }

    public List<ConversationPreviewDTO> getUserConversations(String userId) {
        // TODO: فیلتر کردن لیست کل چت‌ها بر اساس اینکه buyerId یا sellerId برابر با userId باشد
        if ( userId == null){
            throw new UserNotFoundException("ابتدا وارد شوید");
        }
        User user = userRepository.findByID(userId).orElseThrow(()-> new UserNotFoundException("کاربر یافت نشد"));
        if ( !user.isEnabled()){
            throw new UserBannedException("کاربر مسدود است");
        }
        List<Conversation> rawConversations = conversationRepository.findConversationsByUserId(userId);

        return rawConversations.stream()
                .map(conv -> {
                    ConversationPreviewDTO dto = new ConversationPreviewDTO();
                    dto.setConversationId(conv.getId());
                    dto.setAdvertisementId(conv.getAdvertisementId());
                    dto.setLastMessagePreview(conv.getLastMessagePreview());
                    dto.setLastMessageAt(conv.getLastMessageAt());
                    String otherPartyId = userId.equals(conv.getBuyerId()) ? conv.getSellerId() : conv.getBuyerId();
                    userRepository.findByID(otherPartyId).ifPresent(otherUser ->
                            dto.setOtherPartyName(otherUser.getUsername()) // یا getUsername() بسته به مدل شما
                    );
                    advertisementRepository.findByID(conv.getAdvertisementId()).ifPresent(ad ->
                            dto.setTitle(ad.getTitle()) // باید فیلد advertisementTitle را به DTO اضافه کنید
                    );
                    return dto;
                }).sorted( (c1 , c2) -> {
                    if (c1.getLastMessageAt() == null && c2.getLastMessageAt() == null) return 0;
                    if (c1.getLastMessageAt() == null) return 1;
                    if (c2.getLastMessageAt() == null) return -1;
                    return c2.getLastMessageAt().compareTo(c1.getLastMessageAt());
                })
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesInConversation(String conversationId, String userId) {
        // TODO: پیدا کردن چت بر اساس آیدی و بازگرداندن لیست پیام‌های آن (مرتب شده بر اساس تاریخ)
        User user = userRepository.findByID(userId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if ( !user.isEnabled()){
            throw new UserBannedException("کاربر مسدود است");
        }
        Conversation conversation = conversationRepository.findByID(conversationId).orElseThrow(() -> new ConversationNotFoundException("گفت و گو یافت نشد"));
        if ( !user.getId().equals(conversation.getBuyerId()) &&  !user.getId().equals(conversation.getSellerId())){
            throw new NoAcceessException("ما اجازه دسترسی به محتوای این گفت وگو را ندارید");
        }


        return conversation.getMessages().stream()
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
    }
}
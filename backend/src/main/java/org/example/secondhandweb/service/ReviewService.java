package org.example.secondhandweb.service;

import org.example.secondhandweb.Repository.AdvertisementRepository;
import org.example.secondhandweb.Repository.ConversationRepository;
import org.example.secondhandweb.Repository.ReviewRepository;
import org.example.secondhandweb.Repository.UserRepository;
import org.example.secondhandweb.exception.*;
import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.model.Review;
import org.example.secondhandweb.dto.ReviewDTO; // 🟢 New package
import static org.example.secondhandweb.exception.BadRequestException.*;
import static org.example.secondhandweb.exception.ForbiddenException.*;
import static org.example.secondhandweb.exception.NotFoundException.*;
import static org.example.secondhandweb.exception.ConflictException.*;
// import org.example.secondhandweb.model.User;
//import org.example.secondhandweb.repository.AdvertisementRepository;
//import org.example.secondhandweb.repository.ConversationRepository;
//import org.example.secondhandweb.repository.ReviewRepository;
//import org.example.secondhandweb.repository.UserRepository;
import org.example.secondhandweb.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ReviewRepository reviewRepository;
    private final ConversationRepository conversationRepository;
    public ReviewService(UserRepository userRepository , AdvertisementRepository advertisementRepository ,
                         ReviewRepository reviewRepository , ConversationRepository conversationRepository) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
        this.reviewRepository = reviewRepository;
        this.conversationRepository= conversationRepository;
    }


    public synchronized boolean submitReview(ReviewDTO review, String loggedUserId , String advertisementId) {
        if ( review.getScore() > 5 || review.getScore() < 1 ){
            throw new InvalidScoreException("اﻣﺘﯿﺎز ﺑﺎﯾﺪ ﺑﯿﻦ ۱ ﺗﺎ ۵ ﺑﺎﺷﺪ.");
        }
        User user = userRepository.findByID(loggedUserId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if ( !user.isEnabled()){
            throw new UserBannedException("حساب کاربری شما مسدود است و اجازه ثبت نظر ندارید");
        }
        Advertisement advertisement = advertisementRepository.findByID(advertisementId).orElseThrow(() -> new AdvertisementNotFoundException("اگهی یافت نشد"));
        User seller = userRepository.findByID(advertisement.getOwnerId()).orElseThrow(() -> new UserNotFoundException("صاحب اگهی یافت نشد"));

        if (loggedUserId.equals(seller.getId())){
            throw new NoAccessException("شما نمیتوانید به خودتان امتیاز بدهید");
        }
        String reviewId = loggedUserId + "_" + advertisementId;
        if (reviewRepository.existsById(reviewId)) {
            throw new ReviewAlreadyExistsException("شما قبلاً امتیاز خود را برای این آگهی ثبت کرده‌اید");
        }

        String expectedConversationId = loggedUserId +"_"+advertisementId;
        if ( !conversationRepository.existsById(expectedConversationId)){
            throw new NoAccessException("شما تنها در صورتی می‌توانید امتیاز دهید که درباره این آگهی با فروشنده گفت‌وگو کرده باشید.");
        }
        Review review1 = new Review();
        review1.setId(reviewId);
        review1.setReviewerId(loggedUserId);
        review1.setScore(review.getScore());
        review1.setSellerId(seller.getId());
        review1.setAdvertisementId(advertisementId);
        review1.setComment(review.getComment() != null ? review.getComment().trim() : "");

        int currentCount = seller.getTotalRatingsCount();
        double currentAvg = seller.getAverageRating();

        double newAvg = ((currentCount * currentAvg) + review.getScore()) / (currentCount + 1);
        newAvg = Math.round(newAvg * 100.0) / 100.0;
        seller.setTotalRatingsCount(currentCount + 1);
        seller.setAverageRating(newAvg);

        userRepository.save(seller);
        reviewRepository.save(review1);

        return true;
    }

    public List<Review> getReviewsForUser(String targetId , String userId) {
        User user = userRepository.findByID(userId).orElseThrow(() -> new UserNotFoundException("کاربر یافت نشد"));
        if (!user.isEnabled()){
            throw new UserBannedException("شما مسدود هستید");
        }
        userRepository.findByID(targetId)
                .orElseThrow(() -> new UserNotFoundException("فروشنده مورد نظر یافت نشد"));

        return reviewRepository.findBySellerId(targetId);
    }

}
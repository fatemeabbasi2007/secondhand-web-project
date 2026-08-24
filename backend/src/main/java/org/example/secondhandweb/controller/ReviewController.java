package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.exeption.*;
//import org.example.secondhandweb.model.Review;
//import org.example.secondhandweb.model.ReviewDTO;
//import org.example.secondhandweb.model.User;
//import org.example.secondhandweb.service.ReviewService;
import org.example.secondhandweb.model.Review;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.dto.ReviewDTO;
import org.example.secondhandweb.exeption.*;
import org.example.secondhandweb.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("/submit/{advertisementId}")
    public ResponseEntity<?> submitReview(@RequestBody ReviewDTO reviewDto , @PathVariable String advertisementId , HttpSession session) {
        User loggedUser = (User) session.getAttribute("user");
        if ( loggedUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ایتدا وارد شوید"));
        }

        try{
            reviewService.submitReview(reviewDto , loggedUser.getId() , advertisementId);
            return ResponseEntity.ok(new MessageResponse("نظر شما با موفقیت ثبت شد"));
        }catch (InvalidScoreException | ReviewAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }catch(UserNotFoundException | AdvertisementNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (UserBannedException | NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * مشاهده نظرات و امتیازهای یک فروشنده در صفحه پروفایل او
     * GET http://localhost:8080/api/reviews/user/{username}
     */
    @GetMapping("/user/{sellerId}/reviews")
    public ResponseEntity<?> getUserReviews(@PathVariable String sellerId , HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("کاربر یافت نشد"));
        }
        try{
            List<Review> reviews = reviewService.getReviewsForUser(sellerId ,user.getId() );
            return ResponseEntity.ok(reviews);
        }catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (UserBannedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }

    }
}
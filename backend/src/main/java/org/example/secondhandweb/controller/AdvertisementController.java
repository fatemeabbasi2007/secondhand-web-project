package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.exeption.*;
//import org.example.secondhandweb.model.*;
//import org.example.secondhandweb.service.AdvertisementService;
//import org.apache.catalina.User;
import org.example.secondhandweb.dto.AdSearchDTO;
import org.example.secondhandweb.dto.AdminPendingAdDTO;
import org.example.secondhandweb.dto.AdvertisementDetailDTO;
import org.example.secondhandweb.exeption.*;
import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.service.AdvertisementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.secondhandweb.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;


    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }
///////////////////////////////////////////////////////////////////////////

@GetMapping("/own")
public ResponseEntity<?> getOwnAds(HttpSession session) {
    User user = (User) session.getAttribute("user");
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("ابتدا وارد شوید"));
    }
    try {
        // You need this method in AdvertisementService:
        List<AdSearchDTO> ads = advertisementService.getAdvertisementsByOwnerId(user.getId());
        return ResponseEntity.ok(ads);
    } catch (UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }catch (UserBannedException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
}
    @GetMapping("/search")
    public ResponseEntity<?> searchAds(HttpSession session,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        User user =(User) session.getAttribute("user");
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("برای جستجو و مشاهده آگهی ها، ابتدا باید وارد سامانه شوید"));
        }

            List<AdSearchDTO> chosenAds = advertisementService.searchAndFilterActiveAds(keyword , categoryId , city , minPrice , maxPrice);
            return ResponseEntity.ok(chosenAds);

    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAdDetail(@PathVariable String id , HttpSession session) {
        User user =(User) session.getAttribute("user");
        if ( user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد سامانه شوید"));
        }

        try{
            AdvertisementDetailDTO advertisement = advertisementService.getActiveAdvertisementDetail(id , user.getId());
//            System.out.println("salam :::::::::::::::::::");
//            System.out.println(advertisement.getSpecificAttributes());
            return ResponseEntity.ok(advertisement);

        }catch (UserNotFoundException | AdvertisementNotFoundException e ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch(UserBannedException | IllegalArgumentException  e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createAd(@RequestBody Advertisement advertisement, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("لطفاً ابتدا وارد حساب کاربری خود شوید"));
        }

        // لیست عکس‌ها به طور خودکار از داخل بدنه JSON خوانده می‌شود
        List<String> safeImageUrls = (advertisement.getImageUrls() != null) ? advertisement.getImageUrls() : new ArrayList<>();

        try {
            advertisementService.createNewAdvertisement(advertisement, safeImageUrls, loggedInUser.getId());
            return ResponseEntity.ok(new MessageResponse("آگهی با موفقیت در صف بررسی قرار گرفت"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (UserBannedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (PriceNegativeException | TitleInvalidException | InvalidCategoryIdException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("خطای سیستمی رخ داده است: " + e.getMessage()));
        }
    }

    @PutMapping("/own/{advertisementId}")
    public ResponseEntity<?> editOwnAd(@PathVariable String advertisementId,@RequestBody Advertisement updatedAd , HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("کاربر نامعتبر است");
        }
        if ( advertisementId == null || !advertisementId.equals(updatedAd.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("اگهی نامعتبر است");
        }

        try{
            advertisementService.updateOwnAdvertisement(user.getId(), updatedAd);
            return ResponseEntity.ok().body(new MessageResponse("اگهی  با موفقیت به روز رسانی شد"));
        }catch (IllegalArgumentException | InvalidAdvertisementIdException |
                TitleInvalidException | PriceNegativeException | InvalidCategoryIdException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }catch(AdvertisementNotFoundException |UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch ( UserBannedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        }catch (NoAcceessException  e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }


    @DeleteMapping("/own/{adId}")
    public ResponseEntity<?> deleteOwnAd(@PathVariable String adId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }
        try{
            advertisementService.deleteOwnAdvertisement(adId, user.getId());
            return ResponseEntity.ok(new MessageResponse("اگهی با موفقیت حذف شد"));
        }catch (InvalidAdvertisementIdException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }catch (AdvertisementNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch( NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }


    }

    @PatchMapping("/own/{adId}/sold")
    public ResponseEntity<?> markAsSold(@PathVariable String adId, HttpSession session ) {
        User user = (User) session.getAttribute("user");
        if ( user == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }
        try{
            advertisementService.changeAdStatusToSold(adId , user.getId());
            return ResponseEntity.ok(new MessageResponse("اگهی به فروخته شده تغییر پیدا کرد"));
        }catch (InvalidAdvertisementIdException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }catch (AdvertisementNotFoundException| UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch(NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }catch (UserBannedException |IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }
    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingAdsForAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }
        try {
            List<AdminPendingAdDTO> ads = advertisementService.getPendingAdvertisementsForAdmin(user.getId());
            return ResponseEntity.ok(ads);
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch(NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/admin/{advertisementId}/approve")
    public ResponseEntity<?> approveAd(@PathVariable String advertisementId , HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }
        try{
            advertisementService.approveAdvertisement(advertisementId , user.getId());

            return ResponseEntity.ok(new MessageResponse("اگهی تایید شد"));
        }catch (UserNotFoundException | AdvertisementNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }catch (AdvertisementStatusException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/admin/{advertisementId}/reject")
    public ResponseEntity<?> rejectAd(@PathVariable String advertisementId, @RequestParam String reason, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }

        try {
            advertisementService.rejectAdvertisement(advertisementId , reason , user.getId());
            return ResponseEntity.ok(new MessageResponse("اگهی رد شد"));
        }catch (UserNotFoundException|AdvertisementNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }catch (AdvertisementStatusException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/admin/{advertisementId}")
    public ResponseEntity<?> deleteInappropriateAd(@PathVariable String advertisementId , HttpSession session) {
        User user = (User) session.getAttribute("user");
        if ( user == null ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("ابتدا وارد شوید"));
        }
        try{
            advertisementService.deleteInappropriateAdByAdmin(advertisementId , user.getId());
            return ResponseEntity.ok(new MessageResponse("اگهی با موفقیت توسط ادمین حدف شد"));
        }catch(UserNotFoundException | AdvertisementNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }catch (NoAcceessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }
}
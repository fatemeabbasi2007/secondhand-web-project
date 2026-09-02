package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.exeption.*;
//import org.example.secondhandweb.model.*;
//import org.example.secondhandweb.service.AdvertisementService;
//import org.apache.catalina.User;
import org.example.secondhandweb.dto.AdSearchDTO;
import org.example.secondhandweb.dto.AdminPendingAdDTO;
import org.example.secondhandweb.dto.AdvertisementDetailDTO;
import org.example.secondhandweb.exception.*;
import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.service.AdvertisementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.secondhandweb.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;


    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }
///////////////////////////////////////////////////////////////////////////
    private User getAuthenticatedUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ForbiddenException.NoAccessException("لطفا ابتدا وارد حساب کاربری خود شوید");
        }
        return user;
    }


    @GetMapping("/own")
    public ResponseEntity<?> getOwnAds(HttpSession session) {
        User user = getAuthenticatedUser(session);

        List<AdSearchDTO> ads = advertisementService.getAdvertisementsByOwnerId(user.getId());
        return ResponseEntity.ok(ads);

    }
    @GetMapping("/search")
    public ResponseEntity<?> searchAds(HttpSession session,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        getAuthenticatedUser(session);

            List<AdSearchDTO> chosenAds = advertisementService.searchAndFilterActiveAds(keyword , categoryId , city , minPrice , maxPrice);
            return ResponseEntity.ok(chosenAds);

    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAdDetail(@PathVariable String id , HttpSession session) {
        User user = getAuthenticatedUser(session);

            AdvertisementDetailDTO advertisement = advertisementService.getActiveAdvertisementDetail(id , user.getId());

            return ResponseEntity.ok(advertisement);

    }


    @PostMapping("/create")
    public ResponseEntity<?> createAd(@RequestBody Advertisement advertisement, HttpSession session) {
        User user = getAuthenticatedUser(session);
        List<String> safeImageUrls = (advertisement.getImageUrls() != null) ? advertisement.getImageUrls() : new ArrayList<>();


            advertisementService.createNewAdvertisement(advertisement, safeImageUrls, user.getId());
        return ResponseEntity.ok(new MessageResponse("آگهی با موفقیت در صف بررسی قرار گرفت"));
    }

    @PutMapping("/own/{advertisementId}")
    public ResponseEntity<?> editOwnAd(@PathVariable String advertisementId,@RequestBody Advertisement updatedAd , HttpSession session) {
        User user = getAuthenticatedUser(session);
        if (advertisementId == null || !advertisementId.equals(updatedAd.getId())) {
            throw new BadRequestException.InvalidAdvertisementIdException("شناسه آگهی نامعتبر است");
        }

        advertisementService.updateOwnAdvertisement(user.getId(), updatedAd);
        return ResponseEntity.ok(new MessageResponse("آگهی با موفقیت به روز رسانی شد"));
    }


    @DeleteMapping("/own/{adId}")
    public ResponseEntity<?> deleteOwnAd(@PathVariable String adId, HttpSession session) {
        User user = getAuthenticatedUser(session);
        advertisementService.deleteOwnAdvertisement(adId, user.getId());
        return ResponseEntity.ok(new MessageResponse("آگهی با موفقیت حذف شد"));
    }

    @PatchMapping("/own/{adId}/sold")
    public ResponseEntity<?> markAsSold(@PathVariable String adId, HttpSession session ) {
        User user = getAuthenticatedUser(session);
        advertisementService.changeAdStatusToSold(adId, user.getId());
        return ResponseEntity.ok(new MessageResponse("آگهی به فروخته شده تغییر پیدا کرد"));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingAdsForAdmin(HttpSession session) {
        User user = getAuthenticatedUser(session);
        List<AdminPendingAdDTO> ads = advertisementService.getPendingAdvertisementsForAdmin(user.getId());
        return ResponseEntity.ok(ads);
    }

    @PostMapping("/admin/{advertisementId}/approve")
    public ResponseEntity<?> approveAd(@PathVariable String advertisementId , HttpSession session) {
        User user = getAuthenticatedUser(session);
        advertisementService.approveAdvertisement(advertisementId, user.getId());
        return ResponseEntity.ok(new MessageResponse("آگهی تایید شد"));
    }

    @PostMapping("/admin/{advertisementId}/reject")
    public ResponseEntity<?> rejectAd(@PathVariable String advertisementId, @RequestParam String reason, HttpSession session) {
        User user = getAuthenticatedUser(session);
        advertisementService.rejectAdvertisement(advertisementId, reason, user.getId());
        return ResponseEntity.ok(new MessageResponse("آگهی رد شد"));
    }

    @DeleteMapping("/admin/{advertisementId}")
    public ResponseEntity<?> deleteInappropriateAd(@PathVariable String advertisementId , HttpSession session) {
        User user = getAuthenticatedUser(session);
        advertisementService.deleteInappropriateAdByAdmin(advertisementId, user.getId());
        return ResponseEntity.ok(new MessageResponse("آگهی با موفقیت توسط ادمین حذف شد"));
    }
}
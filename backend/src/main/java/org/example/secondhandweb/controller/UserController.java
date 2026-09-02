package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.dto.UserDTO;
//import org.example.secondhandweb.exeption.*;
//import org.example.secondhandweb.model.Advertisement;
//import org.example.secondhandweb.model.User;
//import org.example.secondhandweb.service.UserService;
import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.dto.UserDTO;
import org.example.secondhandweb.exception.*;
import org.example.secondhandweb.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private User getAuthenticatedUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ForbiddenException.NoAccessException("لطفاً ابتدا وارد سیستم شوید.");
        }
        return user;
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = getAuthenticatedUser(session);
        UserDTO dto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNum(),
                user.getFullName(),
                user.getRole(),
                user.isEnabled(),
                user.getFavoriteAdIds(),
                user.getAverageRating(),
                user.getTotalRatingsCount()
        );

        return ResponseEntity.ok(dto);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok(new MessageResponse("ثبت‌نام با موفقیت انجام شد"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        User user = userService.loginUser(loginRequest.username(), loginRequest.password())
                .orElseThrow(() -> new NotFoundException.UserNotFoundException("نام کاربری یا رمز عبور اشتباه است"));

        session.setAttribute("user", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        getAuthenticatedUser(session);
        session.invalidate();
        return ResponseEntity.ok(new MessageResponse("خروج از سیستم موفقیت‌آمیز بود."));
    }

    @PostMapping("/{userId}/favorites/{adId}")
    public ResponseEntity<?> addToFavorites(@PathVariable String userId, @PathVariable String adId) {
        userService.addAdvertisementToFavorites(userId, adId);
        return ResponseEntity.ok(new MessageResponse("با موفقیت به علاقه‌مندی‌ها اضافه شد"));
    }

    @DeleteMapping("/{userId}/favorites/{adId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable String userId, @PathVariable String adId) {
        userService.removeAdvertisementFromFavorites(userId, adId);
        return ResponseEntity.ok(new MessageResponse("با موفقیت از علاقه‌مندی‌ها حذف شد"));
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<?> getFavorites(@PathVariable String userId) {
        List<Advertisement> favorites = userService.getUserFavoriteAdIds(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<?> getAllUsersForAdmin(HttpSession session) {
        User currentUser = getAuthenticatedUser(session);
        List<User> users = userService.getAllUsersForAdmin(currentUser.getId());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/admin/block/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable String userId, HttpSession session) {User currentUser = getAuthenticatedUser(session);
        userService.blockUser(userId, currentUser.getId());
        return ResponseEntity.ok(new MessageResponse("کاربر با موفقیت مسدود شد"));
    }

    /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PatchMapping("/admin/unblock/{userId}")
    public ResponseEntity<?> unblockUser(@PathVariable String userId, HttpSession session) {
        User currentUser = getAuthenticatedUser(session);
        userService.unblockUser(userId, currentUser.getId());
        return ResponseEntity.ok(new MessageResponse("کاربر با موفقیت رفع مسدودیت شد"));
    }
}
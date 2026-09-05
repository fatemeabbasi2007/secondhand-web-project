package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
import org.example.secondhandweb.dto.RegisterRequestDTO;
import org.example.secondhandweb.dto.UserDTO;
import org.example.secondhandweb.exception.ForbiddenException;
import org.example.secondhandweb.model.Advertisement;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.mapper.UserMapper;
import org.example.secondhandweb.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;
    }

    private User getAuthenticatedUser(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new ForbiddenException.NoAccessException(
                    "لطفاً ابتدا وارد سیستم شوید."
            );
        }

        return user;
    }

    // ---------------------------------------------------------
    // Current user
    // ---------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        return ResponseEntity.ok(
                userMapper.toDTO(currentUser)
        );
    }

    // ---------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @RequestBody RegisterRequestDTO request) {

        userService.registerUser(request);

        return ResponseEntity.ok(
                new MessageResponse(
                        "ثبت‌نام با موفقیت انجام شد"
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestBody LoginRequest loginRequest,
            HttpSession session) {

        User user = userService.loginUser(
                loginRequest.username(),
                loginRequest.password()
        );

        session.setAttribute("user", user);

        return ResponseEntity.ok(
                userMapper.toDTO(user)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            HttpSession session) {

        getAuthenticatedUser(session);

        session.invalidate();

        return ResponseEntity.ok(
                new MessageResponse(
                        "خروج از سیستم موفقیت‌آمیز بود."
                )
        );
    }

    // ---------------------------------------------------------
    // Favorites
    // ---------------------------------------------------------

    @PostMapping("/me/favorites/{adId}")
    public ResponseEntity<MessageResponse> addToFavorites(
            @PathVariable String adId,
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        userService.addAdvertisementToFavorites(
                currentUser,
                adId
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "با موفقیت به علاقه‌مندی‌ها اضافه شد"
                )
        );
    }

    @DeleteMapping("/me/favorites/{adId}")
    public ResponseEntity<MessageResponse> removeFromFavorites(
            @PathVariable String adId,
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        userService.removeAdvertisementFromFavorites(
                currentUser,
                adId
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "با موفقیت از علاقه‌مندی‌ها حذف شد"
                )
        );
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<List<Advertisement>> getFavorites(
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        List<Advertisement> favorites =
                userService.getUserFavoriteAdIds(currentUser);

        return ResponseEntity.ok(favorites);
    }

    // ---------------------------------------------------------
    // Admin
    // ---------------------------------------------------------

    @GetMapping("/admin/all-users")
    public ResponseEntity<List<UserDTO>> getAllUsersForAdmin(
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        List<UserDTO> users = userService
                .getAllUsersForAdmin(currentUser)
                .stream()
                .map(userMapper::toDTO)
                .toList();

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/admin/block/{userId}")
    public ResponseEntity<MessageResponse> blockUser(
            @PathVariable String userId,
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        userService.blockUser(
                userId,
                currentUser
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "کاربر با موفقیت مسدود شد"
                )
        );
    }

    @PatchMapping("/admin/unblock/{userId}")
    public ResponseEntity<MessageResponse> unblockUser(
            @PathVariable String userId,
            HttpSession session) {

        User currentUser = getAuthenticatedUser(session);

        userService.unblockUser(
                userId,
                currentUser
        );

        return ResponseEntity.ok(
                new MessageResponse(
                        "کاربر با موفقیت رفع مسدودیت شد"
                )
        );
    }
}
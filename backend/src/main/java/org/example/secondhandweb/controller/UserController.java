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
import org.example.secondhandweb.exeption.*;
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

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("کاربر وارد نشده است"));
        }

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
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(new MessageResponse("done successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            User user = userService.loginUser(loginRequest.username(), loginRequest.password()).orElseThrow(() -> new UserNotFoundException("what .."));
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (WrongPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        } catch (UserBannedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }catch ( IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("هیچ کاربری وارد سیستم نشده است"));
        }
        session.invalidate();
        return ResponseEntity.ok(new MessageResponse("خروج از سیستم موفقیت‌آمیز بود."));
    }

    @PostMapping("/{userId}/favorites/{adId}")
    public ResponseEntity<?> addToFavorites(@PathVariable String userId, @PathVariable String adId) {
        try {
            userService.addAdvertisementToFavorites(userId, adId);
            return ResponseEntity.ok(new MessageResponse("با موفقیت به علاقه مندی ها اضافه شد"));
        } catch (UserNotFoundException | AdvertisementNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (AdAlreadyFavoriteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));

        }
    }

    @DeleteMapping("/{userId}/favorites/{adId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable String userId, @PathVariable String adId) {
        try {
            userService.removeAdvertisementFromFavorites(userId, adId);
            return ResponseEntity.ok(new MessageResponse("با موفقیت از علاقه مندی ها حذف شد"));
        } catch (UserNotFoundException | AdvertisementNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (AdNotFavException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<?> getFavorites(@PathVariable String userId) {
        try {
            List<Advertisement> list = userService.getUserFavoriteAdIds(userId);
            return ResponseEntity.ok(list);
        } catch (UserNotFoundException | AdvertisementNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<?> getAllUsersForAdmin(HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("لطفاً ابتدا وارد سیستم شوید."));
            }
            List<User> list = userService.getAllUsersForAdmin(currentUser.getId());
            return ResponseEntity.ok(list);
        } catch (NoAcceessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/admin/block/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable String userId, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("لطفاً ابتدا وارد سیستم شوید."));
            }

            userService.blockUser(userId, currentUser.getId());
            return ResponseEntity.ok(new MessageResponse("کاربر با موفقیت مسدود شد"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (NoAcceessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }
    }

    /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PatchMapping("/admin/unblock/{userId}")
    public ResponseEntity<?> unblockUser(@PathVariable String userId, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("لطفاً ابتدا وارد سیستم شوید."));
            }

            userService.unblockUser(userId, currentUser.getId());
            return ResponseEntity.ok(new MessageResponse("کاربر با موفقیت رفع مسدودیت شد"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (NoAcceessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));

        }
    }
}
package org.example.secondhandweb.controller;

import jakarta.servlet.http.HttpSession;
//import org.example.secondhandweb.dto.ErrorResponse;
import org.example.secondhandweb.controller.ErrorResponse;
import org.example.secondhandweb.model.User;
import org.example.secondhandweb.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("ابتدا وارد شوید"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("فایلی انتخاب نشده"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("فقط فایل تصویر مجاز است"));
        }

        try {
            String url = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("آپلود تصویر ناموفق بود: " + e.getMessage()));
        }
    }
}
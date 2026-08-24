package org.example.secondhandweb.controller;

import org.example.secondhandweb.model.AttributeRule;
import org.example.secondhandweb.model.Category;
import org.example.secondhandweb.service.AdvertisementService;
import org.example.secondhandweb.Repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final AdvertisementService advertisementService;
    private final CategoryRepository categoryRepository;

    public CategoryController(AdvertisementService advertisementService,
                              CategoryRepository categoryRepository) {
        this.advertisementService = advertisementService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/{id}/attributes")
    public ResponseEntity<List<AttributeRule>> getCategoryAttributes(@PathVariable String id) {
        return ResponseEntity.ok(advertisementService.getRulesForCategory(id));
    }
}
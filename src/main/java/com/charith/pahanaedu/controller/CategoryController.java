package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}

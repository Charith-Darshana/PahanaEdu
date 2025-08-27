package com.charith.pahanaedu.service;

import com.charith.pahanaedu.entity.Category;
import com.charith.pahanaedu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .toList();
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }
}

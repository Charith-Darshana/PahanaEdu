package com.charith.pahanaedu.service;

import com.charith.pahanaedu.entity.Category;

import java.util.List;

public interface CategoryService {
    List<String> getAllCategories();
    Category getCategoryByName(String name);
}

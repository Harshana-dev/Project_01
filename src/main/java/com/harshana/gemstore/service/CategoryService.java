package com.harshana.gemstore.service;

import com.harshana.gemstore.entity.Category;
import com.harshana.gemstore.repository.CategoryRepository;
import com.harshana.gemstore.repository.GemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final GemRepository gemRepository; // ✅ add this

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {

        if (gemRepository.existsByCategoryId(id)) {
            throw new IllegalStateException(
                    "Cannot delete category. There are gems under this category."
            );
        }

        categoryRepository.deleteById(id);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
}
package com.harshana.gemstore.controller;

import com.harshana.gemstore.entity.Category;
import com.harshana.gemstore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CategoryService categoryService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // --- CATEGORY MANAGEMENT ---
    @GetMapping("/categories")
    public String categoryList(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/manage-categories";
    }

    @GetMapping("/categories/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/add-category";
    }

    @PostMapping("/categories/add")
    public String saveCategory(@ModelAttribute Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return "redirect:/admin/categories?success=deleted";
        } catch (IllegalStateException ex) {
            return "redirect:/admin/categories?error=hasGems";
        }
    }

}
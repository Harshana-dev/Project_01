package com.harshana.gemstore.controller;

import com.harshana.gemstore.service.CategoryService;
import com.harshana.gemstore.service.GemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class PublicController {

    private final GemService gemService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minCarat,
            @RequestParam(required = false) Double maxCarat,
            Model model
    ) {
        model.addAttribute("categories", categoryService.getAllCategories());

        // Keep values to re-fill the form
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minCarat", minCarat);
        model.addAttribute("maxCarat", maxCarat);

        model.addAttribute("gems", gemService.filterGems(categoryId, minPrice, maxPrice, minCarat, maxCarat));
        return "public/gem-list";
    }

    @GetMapping("/gem/{id}")
    public String gemDetails(@PathVariable Long id, Model model) {
        model.addAttribute("gem", gemService.getGemById(id));
        return "public/gem-details";
    }
}

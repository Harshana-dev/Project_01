package com.harshana.gemstore.controller;

import com.harshana.gemstore.entity.Gem;
import com.harshana.gemstore.service.CategoryService;
import com.harshana.gemstore.service.GemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/admin/gems")
@RequiredArgsConstructor
public class GemController {

    private final GemService gemService;
    private final CategoryService categoryService;

    // GET /admin/gems
    @GetMapping
    public String showAllGems(Model model) {
        model.addAttribute("gems", gemService.getAllGems());
        return "admin/manage-gems";
    }

    // GET /admin/gems/add
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("gem", new Gem());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/add-gem";
    }

    // POST /admin/gems/add
    @PostMapping("/add")
    public String saveGem(
            @ModelAttribute Gem gem,
            @RequestParam("img1") MultipartFile img1,
            @RequestParam("img2") MultipartFile img2,
            @RequestParam("img3") MultipartFile img3,
            @RequestParam("videoFile") MultipartFile videoFile
    ) throws IOException {

        String uploadDir = getUploadDir();
        ensureDirExists(uploadDir);

        if (!img1.isEmpty()) gem.setImage1(saveFile(img1, uploadDir));
        if (!img2.isEmpty()) gem.setImage2(saveFile(img2, uploadDir));
        if (!img3.isEmpty()) gem.setImage3(saveFile(img3, uploadDir));
        if (!videoFile.isEmpty()) gem.setVideo(saveFile(videoFile, uploadDir));

        gem.setStatus("AVAILABLE");
        gemService.saveGem(gem);

        return "redirect:/admin/gems";
    }

    // ✅ GET /admin/gems/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Gem gem = gemService.getGemById(id);
        if (gem == null) {
            return "redirect:/admin/gems";
        }

        model.addAttribute("gem", gem);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/edit-gem";
    }

    // ✅ UPDATED POST /admin/gems/edit
    // Fixed: Accepted MultipartFile to resolve 400 Bad Request
    @PostMapping("/edit")
    public String updateGem(
            @ModelAttribute Gem gem,
            @RequestParam("gemImage") MultipartFile imageFile
    ) throws IOException {

        // 1. Get existing gem data from database to keep old images if not replacing
        Gem existingGem = gemService.getGemById(gem.getId());

        // 2. Handle file upload if a new file was selected
        if (!imageFile.isEmpty()) {
            String uploadDir = getUploadDir();
            ensureDirExists(uploadDir);

            // Delete old file if it exists
            deleteFileIfExists(uploadDir, existingGem.getImage1());

            // Save new file and update gem object
            String fileName = saveFile(imageFile, uploadDir);
            gem.setImage1(fileName);
        } else {
            // Keep the old image filename if no new file was uploaded
            gem.setImage1(existingGem.getImage1());
        }

        // Ensure other fields not in the form are retained
        gem.setImage2(existingGem.getImage2());
        gem.setImage3(existingGem.getImage3());
        gem.setVideo(existingGem.getVideo());
        gem.setStatus(existingGem.getStatus());

        // 3. Save the updated gem
        gemService.saveGem(gem);
        return "redirect:/admin/gems";
    }

    // ✅ GET /admin/gems/delete/{id}
    @GetMapping("/delete/{id}")
    public String deleteGem(@PathVariable Long id) {
        try {
            // Get gem first (so we can delete uploaded files too)
            Gem gem = gemService.getGemById(id);

            // Delete DB record
            gemService.deleteGem(id);

            // Delete uploaded files (optional but recommended)
            if (gem != null) {
                String uploadDir = getUploadDir();
                deleteFileIfExists(uploadDir, gem.getImage1());
                deleteFileIfExists(uploadDir, gem.getImage2());
                deleteFileIfExists(uploadDir, gem.getImage3());
                deleteFileIfExists(uploadDir, gem.getVideo());
            }

            return "redirect:/admin/gems?success=deleted";
        } catch (IllegalStateException ex) {
            // Use this if you block deletion in service (ex: gem is in orders)
            return "redirect:/admin/gems?error=cannotDelete";
        } catch (Exception ex) {
            // Any unexpected error -> also show message instead of whitelabel
            return "redirect:/admin/gems?error=cannotDelete";
        }
    }

    // -------------------------
    // Helpers
    // -------------------------
    private String getUploadDir() {
        return System.getProperty("user.dir") + "/uploads/";
    }

    private void ensureDirExists(String uploadDir) {
        File directory = new File(uploadDir);
        if (!directory.exists()) directory.mkdirs();
    }

    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(uploadDir + fileName));
        return fileName;
    }

    private void deleteFileIfExists(String uploadDir, String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        File f = new File(uploadDir + fileName);
        if (f.exists()) {
            // ignore result; we don't want delete failures to crash the app
            f.delete();
        }
    }
}
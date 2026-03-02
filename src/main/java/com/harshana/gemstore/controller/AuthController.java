package com.harshana.gemstore.controller;

import com.harshana.gemstore.entity.User;
import com.harshana.gemstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ✅ Keep registration API as JSON (still fine)
    @PostMapping("/register")
    @ResponseBody
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // ✅ Login page for browser (Thymeleaf)
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // templates/auth/login.html
    }


}

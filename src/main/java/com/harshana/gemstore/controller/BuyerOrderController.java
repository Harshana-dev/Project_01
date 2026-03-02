package com.harshana.gemstore.controller;

import com.harshana.gemstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BuyerOrderController {

    private final OrderRepository orderRepository;

    // Track form
    @GetMapping("/track")
    public String trackForm() {
        return "public/track-order";
    }

    // Track result (single order)
    @PostMapping("/track")
    public String trackResult(@RequestParam Long orderId,
                              @RequestParam String phone,
                              Model model) {

        var orderOpt = orderRepository.findByIdAndPhone(orderId, phone);
        if (orderOpt.isEmpty()) {
            model.addAttribute("error", "Order not found. Please check Order ID and Phone.");
            return "public/track-order";
        }

        model.addAttribute("order", orderOpt.get());
        return "public/buyer-order-details";
    }

    // “My Orders” by phone
    @GetMapping("/my-orders")
    public String myOrdersPage() {
        return "public/my-orders";
    }

    @PostMapping("/my-orders")
    public String myOrdersResult(@RequestParam String phone, Model model) {
        model.addAttribute("phone", phone);
        model.addAttribute("orders", orderRepository.findByPhoneOrderByCreatedAtDesc(phone));
        return "public/my-orders";
    }
}
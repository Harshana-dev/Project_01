package com.harshana.gemstore.controller;

import com.harshana.gemstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderRepository orderRepository;

    @GetMapping("/order/status/{id}")
    public String status(@PathVariable Long id, Model model) {
        var order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "public/order-status";
    }
}
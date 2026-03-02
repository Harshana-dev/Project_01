package com.harshana.gemstore.controller;

import com.harshana.gemstore.entity.OrderStatus;
import com.harshana.gemstore.entity.PaymentMethod;
import com.harshana.gemstore.repository.OrderRepository;
import com.harshana.gemstore.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/manage-orders";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        var order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-details";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        var order = orderRepository.findById(id).orElseThrow();
        order.setOrderStatus(status);
        orderRepository.save(order);

        // Email buyer when status changes
        if (order.getCustomerEmail() != null && !order.getCustomerEmail().isBlank()) {
            emailService.send(
                    order.getCustomerEmail(),
                    "Order Update - GemStore (Order #" + order.getId() + ")",
                    "Hi " + order.getCustomerName() + ",\n\n" +
                            "Your order status has been updated to: " + status + "\n\n" +
                            "Order ID: " + order.getId() + "\n" +
                            "Thank you."
            );
        }

        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/verify-receipt")
    public String verifyReceipt(@PathVariable Long id) {
        var order = orderRepository.findById(id).orElseThrow();

        // Only meaningful for bank transfer
        if (order.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            order.setReceiptVerified(true);
            orderRepository.save(order);
        }

        return "redirect:/admin/orders/" + id;
    }
}
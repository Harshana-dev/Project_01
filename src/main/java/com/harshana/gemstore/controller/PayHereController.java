package com.harshana.gemstore.controller;

import com.harshana.gemstore.entity.Order;
import com.harshana.gemstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payhere")
public class PayHereController {

    private final OrderService orderService;

    @Value("${payhere.merchantId}")
    private String merchantId;

    @Value("${payhere.currency:LKR}")
    private String currency;

    // Show a page that opens PayHere JS payment (popup/redirect handled by JS)
    @GetMapping("/pay/{orderId}")
    public String pay(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);

        // Basic info for the JS SDK payment object
        model.addAttribute("merchantId", merchantId);
        model.addAttribute("orderId", order.getId());
        model.addAttribute("amount", String.format("%.2f", order.getTotalAmount()));
        model.addAttribute("currency", currency);

        // Customer info (use what you already save)
        model.addAttribute("customerName", order.getCustomerName());
        model.addAttribute("phone", order.getPhone());
        model.addAttribute("address", order.getDeliveryAddress());

        // Item title
        model.addAttribute("items", "Gem Order #" + order.getId());

        return "public/payhere-pay";
    }

    // Called by JS after payment success to mark the order as paid
    @PostMapping("/confirm")
    @ResponseBody
    public String confirm(@RequestParam Long orderId,
                          @RequestParam(required = false) String paymentId) {

        // paymentId may not always be available from the JS callback in a simple setup
        // (for production you should verify server-side using PayHere notification/webhook)
        orderService.markAsPaid(orderId, paymentId != null ? paymentId : "PAYHERE_SUCCESS");

        return "OK";
    }

    @GetMapping("/success/{orderId}")
    public String success(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "public/payment-success";
    }

    @GetMapping("/cancel/{orderId}")
    public String cancel(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "public/payment-cancel";
    }
}
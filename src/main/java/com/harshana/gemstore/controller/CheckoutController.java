package com.harshana.gemstore.controller;

import com.harshana.gemstore.dto.CartItem;
import com.harshana.gemstore.entity.PaymentMethod;
import com.harshana.gemstore.service.EmailService;
import com.harshana.gemstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderService orderService;
    private final EmailService emailService;

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        return (List<CartItem>) session.getAttribute("CART");
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        double total = cart.stream()
                .mapToDouble(i -> i.getGem().getPrice() * i.getQuantity())
                .sum();

        model.addAttribute("total", total);
        return "public/checkout";
    }
    @PostMapping("/checkout")
    public String placeOrder(
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @RequestParam String phone,
            @RequestParam String deliveryAddress,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam(required = false) MultipartFile receipt,
            HttpSession session,
            Model model
    ) throws IOException {

        List<CartItem> cart = getCart(session);
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        String receiptFileName = null;

        if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
            if (receipt == null || receipt.isEmpty()) {
                model.addAttribute("error", "Please upload bank transfer receipt.");
                model.addAttribute("total",
                        cart.stream().mapToDouble(i -> i.getGem().getPrice() * i.getQuantity()).sum());
                return "public/checkout";
            }

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            receiptFileName = UUID.randomUUID() + "_" + receipt.getOriginalFilename();
            receipt.transferTo(new File(uploadDir + receiptFileName));
        }

        var order = orderService.createOrder(
                customerName,
                customerEmail,
                phone,
                deliveryAddress,
                paymentMethod,
                receiptFileName,
                cart
        );

        // ✅ Using sendSafe for buyer notification to prevent crashes
        emailService.sendSafe(
                order.getCustomerEmail(),
                "Order Received - GemStore (Order #" + order.getId() + ")",
                "Hi " + order.getCustomerName() + ",\n\n" +
                        "Your order has been received.\n" +
                        "Order ID: " + order.getId() + "\n" +
                        "Total: Rs " + order.getTotalAmount() + "\n" +
                        "Payment: " + order.getPaymentMethod() + "\n" +
                        "Status: " + order.getOrderStatus() + "\n\n" +
                        "Thank you!"
        );

        // ✅ Using sendSafe for Admin notify to prevent crashes
        emailService.sendSafe(
                "admin@gemstore.com",
                "New Order Received (Order #" + order.getId() + ")",
                "New order received.\n" +
                        "Order ID: " + order.getId() + "\n" +
                        "Customer: " + order.getCustomerName() + " (" + order.getPhone() + ")\n" +
                        "Total: Rs " + order.getTotalAmount()
        );

        // clear cart
        session.setAttribute("CART", null);

        if (paymentMethod == PaymentMethod.CARD) {
            session.setAttribute("CART",null);
            return "redirect:/payhere/pay/" + order.getId();
        }

        session.setAttribute("CART",null);
        return "redirect:/order/success/" + order.getId();
    }

    @GetMapping("/order/success/{orderId}")
    public String success(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "public/order-success";
    }
}
package com.harshana.gemstore.service;

import com.harshana.gemstore.dto.CartItem;
import com.harshana.gemstore.entity.*;
import com.harshana.gemstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // Added String email parameter here
    public Order createOrder(String name, String email, String phone, String address, PaymentMethod paymentMethod,
                             String receiptFileNameOrNull,
                             List<CartItem> cartItems) {

        double total = cartItems.stream()
                .mapToDouble(i -> i.getGem().getPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .customerName(name)
                .customerEmail(email) // ✅ Set the email from the parameter
                .phone(phone)
                .deliveryAddress(address)
                .paymentMethod(paymentMethod)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(total)
                .createdAt(LocalDateTime.now())
                .bankReceiptImage(receiptFileNameOrNull)
                .paid(false)
                .receiptVerified(false) // ✅ Default to false for BANK_TRANSFER verification
                .paymentId(null)
                .build();

        for (CartItem ci : cartItems) {
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .gem(ci.getGem())
                    .quantity(ci.getQuantity())
                    .price(ci.getGem().getPrice())
                    .build();
            order.getItems().add(item);
        }

        return orderRepository.save(order);
    }

    // ✅ used by PayHereController (JS SDK flow)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    // ✅ called when PayHere payment completes
    public void markAsPaid(Long orderId, String paymentId) {
        Order order = getOrderById(orderId);

        order.setPaid(true);
        order.setPaymentId(paymentId);
        order.setOrderStatus(OrderStatus.APPROVED);

        orderRepository.save(order);
    }
}
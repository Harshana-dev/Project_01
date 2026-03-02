package com.harshana.gemstore.controller;

import com.harshana.gemstore.repository.OrderRepository;
import com.harshana.gemstore.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class InvoiceController {

    private final OrderRepository orderRepository;
    private final InvoiceService invoiceService;

    // Buyer downloads invoice using Order ID + Phone (same as tracking)
    @GetMapping("/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@RequestParam Long orderId, @RequestParam String phone) {

        var orderOpt = orderRepository.findByIdAndPhone(orderId, phone);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var pdf = invoiceService.generateInvoicePdf(orderOpt.get());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("invoice-order-" + orderId + ".pdf")
                .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
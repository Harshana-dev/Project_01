package com.harshana.gemstore.controller;

import com.harshana.gemstore.dto.CartItem;
import com.harshana.gemstore.entity.Gem;
import com.harshana.gemstore.service.GemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final GemService gemService;

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("CART");
        if (cartObj == null) {
            List<CartItem> cart = new ArrayList<>();
            session.setAttribute("CART", cart);
            return cart;
        }
        return (List<CartItem>) cartObj;
    }

    @PostMapping("/cart/add/{gemId}")
    public String addToCart(@PathVariable Long gemId, HttpSession session) {
        Gem gem = gemService.getGemById(gemId);
        if (gem == null) return "redirect:/";

        List<CartItem> cart = getCart(session);

        // Gems typically quantity 1, but allow multiple in case business wants.
        for (CartItem item : cart) {
            if (item.getGem().getId().equals(gemId)) {
                item.setQuantity(item.getQuantity() + 1);
                return "redirect:/cart";
            }
        }

        cart.add(new CartItem(gem, 1));
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        double total = cart.stream().mapToDouble(i -> i.getGem().getPrice() * i.getQuantity()).sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "public/cart";
    }

    @GetMapping("/cart/remove/{gemId}")
    public String removeFromCart(@PathVariable Long gemId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> i.getGem().getId().equals(gemId));
        return "redirect:/cart";
    }

    @GetMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        session.setAttribute("CART", new ArrayList<CartItem>());
        return "redirect:/cart";
    }
}
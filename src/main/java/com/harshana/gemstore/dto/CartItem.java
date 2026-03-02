package com.harshana.gemstore.dto;

import com.harshana.gemstore.entity.Gem;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CartItem {
    private Gem gem;
    private int quantity;
}
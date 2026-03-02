package com.harshana.gemstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double carat;

    private double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String status;

    private String image1;
    private String image2;
    private String image3;

    private String video;
}

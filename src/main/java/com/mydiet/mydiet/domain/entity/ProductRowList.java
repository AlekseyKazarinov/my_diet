package com.mydiet.mydiet.domain.entity;

import jakarta.persistence.*;

@Entity
public class ProductRowList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}

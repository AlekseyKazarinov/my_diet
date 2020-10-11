package com.mydiet.mydiet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/products")
public class ProductController {

    @GetMapping
    public List<String> getProducts() {
        var productsList = new ArrayList<String>();
        productsList.add("Honey");
        productsList.add("Almond");
        return productsList;
    }

    @PostMapping
    public String createProduct() {
        return "Product is saved successfully";
    }
}

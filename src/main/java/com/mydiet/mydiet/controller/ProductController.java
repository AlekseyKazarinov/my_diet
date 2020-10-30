package com.mydiet.mydiet.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/products")
@Api(tags = "Products")
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

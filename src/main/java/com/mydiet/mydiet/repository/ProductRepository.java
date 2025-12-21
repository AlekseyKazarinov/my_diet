package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    public Optional<Product> findProductByName(String name);
    public List<Product> findProductByProductType(ProductType productType);

}

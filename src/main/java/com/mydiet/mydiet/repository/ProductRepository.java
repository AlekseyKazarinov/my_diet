package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Product;
import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.domain.exception.ValidationException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    public Optional<Product> findProductByName(String name);
    public List<Product> findProductByProductType(ProductType productType);

}

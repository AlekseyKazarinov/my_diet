package com.mydiet.mydiet.repository;


import com.mydiet.mydiet.infrastructure.ConversionUnits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversionUnitsRepository extends JpaRepository<ConversionUnits, Long> {

    @Query(value = "SELECT * FROM CONVERSION_UNITS WHERE PRODUCT_ID = ?1", nativeQuery = true)
    Optional<ConversionUnits> findByProductId(Long productId);

    @Query(value = "SELECT ?1 FROM CONVERSION_UNITS WHERE PRODUCT_ID = ?2", nativeQuery = true)
    List<Double> findConversionCoefficients(String unitName, Long product_id);

}

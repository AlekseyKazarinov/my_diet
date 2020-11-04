package com.mydiet.mydiet.repository;


import com.mydiet.mydiet.infrastructure.UnitConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnitConversionRepository extends JpaRepository<UnitConversion, Long> {

    @Query(value = "SELECT ?1 FROM UNIT_CONVERSION WHERE PRODUCT_ID = ?2", nativeQuery = true)
    List<Double> findConversionCoefficients(String unitName, Long product_id);

}

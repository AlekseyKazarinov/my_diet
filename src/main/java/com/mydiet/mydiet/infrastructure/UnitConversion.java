package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.Product;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "UNIT_CONVERSION")
@Data
public class UnitConversion {

    @Id
    private Long id;

    @Column(unique = true)
    @OneToOne(orphanRemoval = true)
    private Product product;

    private Double teaspoon;
    private Double tablespoon;
    private Double glass;
    private Double cup;
    private Double pinch;
    private Double piece;
    private Double drop;

}

package com.mydiet.mydiet.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mydiet.mydiet.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "CONVERSION_UNITS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionUnits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    @JoinColumn(name = "PRODUCT_ID", unique = true)
    @OneToOne(fetch = LAZY, orphanRemoval = true)
    @JsonProperty(access = READ_ONLY)
    private Product product;

    private Double teaspoon;
    private Double tablespoon;
    private Double glass;
    private Double cup;
    private Double pinch;
    private Double piece;
    private Double drop;

}

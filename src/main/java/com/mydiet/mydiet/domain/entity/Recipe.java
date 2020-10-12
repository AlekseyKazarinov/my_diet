package com.mydiet.mydiet.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long         id;

    private String       name;
    private String       imageId;
    private String       description;

    //private List<String> ingredients;
    private Integer      kkal;
    private FoodTime     foodTime;
    private String       dayNumber;
    private Integer      programNumber;

}

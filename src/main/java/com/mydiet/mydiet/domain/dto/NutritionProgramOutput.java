package com.mydiet.mydiet.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mydiet.mydiet.domain.entity.DailyDiet;
import com.mydiet.mydiet.domain.entity.Image;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.time.Instant;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@Builder
public class NutritionProgramOutput {
    private Long number;

    private Long version;

    private Instant lastModifiedAt;

    private String name;
    private String description;

    private Image image;
    private String backgroundColour;

    private Short numberOfMeals;

    private List<DailyDietOutput> dailyDietList;

}

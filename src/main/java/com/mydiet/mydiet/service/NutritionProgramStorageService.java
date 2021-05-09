package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NutritionProgramStorageService {

    private final NutritionProgramRepository nutritionProgramRepository;

    public NutritionProgram getProgramOrElseThrow(Long programNumber) {
        return nutritionProgramRepository.findById(programNumber)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Nutrition Program #%s does not exist", programNumber))
                );
    }

}

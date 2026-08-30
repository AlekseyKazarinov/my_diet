package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.exception.NotFoundException;
import com.mydiet.mydiet.repository.NutritionProgramRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Slf4j
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

    public NutritionProgram getProgramOrElseThrow(String name) {
        return nutritionProgramRepository.findNutritionProgramByName(name)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Nutrition Program with name: #%s does not exist", name))
                );
    }

    public NutritionProgram saveIfOriginal(NutritionProgram nutritionProgram) {
        var example = Example.of(nutritionProgram);
        var optionalStoredProgram = nutritionProgramRepository.findOne(example);

        if (optionalStoredProgram.isPresent()) {
            log.info("Nutrition Program with name: {} already exists", nutritionProgram.getName());
            return optionalStoredProgram.get();
        }

        return nutritionProgramRepository.save(nutritionProgram);
    }

}

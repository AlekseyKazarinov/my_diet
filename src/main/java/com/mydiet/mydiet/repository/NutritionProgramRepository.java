package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.NutritionProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutritionProgramRepository extends JpaRepository<NutritionProgram, Long> {

    public Optional<NutritionProgram> findNutritionProgramByName(String name);
}

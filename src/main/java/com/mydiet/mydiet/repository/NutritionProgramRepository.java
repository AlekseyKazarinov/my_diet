package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutritionProgramRepository extends JpaRepository<NutritionProgram, Long> {

    public Optional<NutritionProgram> findNutritionProgramByName(String name);
    public Optional<NutritionProgram> findProgramByNumberAndStatus(Long number, Status status);
    public Optional<NutritionProgram> findProgramByNameAndStatus(String name, Status status);
}

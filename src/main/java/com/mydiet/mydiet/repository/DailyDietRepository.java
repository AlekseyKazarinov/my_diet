package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.DailyDiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DailyDietRepository extends JpaRepository<DailyDiet, Long>, DailyDietRepositoryCustom {

}

package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Language;
import com.mydiet.mydiet.domain.entity.Lifestyle;
import com.mydiet.mydiet.domain.entity.NutritionProgram;
import com.mydiet.mydiet.domain.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface NutritionProgramRepository extends JpaRepository<NutritionProgram, Long> {

    public Optional<NutritionProgram> findNutritionProgramByName(String name);
    public Optional<NutritionProgram> findProgramByNumberAndStatus(Long number, Status status);
    public Optional<NutritionProgram> findProgramByNameAndStatus(String name, Status status);

    public List<NutritionProgram> findAllByStatusAndKcalIsBetween(Status status, Integer minKcal, Integer maxKcal);
    public Page<NutritionProgram> findAllByLanguageAndStatusAndKcalGreaterThanOrderByKcalAsc(Language language, Status status, Integer kcal, Pageable pageRequest);
    public Page<NutritionProgram> findAllByLanguageAndStatusAndKcalLessThanEqualOrderByKcalDesc(Language language, Status status, Integer kcal, Pageable pageRequest);

    public List<NutritionProgram> findByLifestyles(Set<Lifestyle> lifestyles);
    public Page<NutritionProgram> findAllByStatusAndLifestyles(Status status, Set<Lifestyle> lifestyles, Pageable pageable);
    public Page<NutritionProgram> findAllByLanguageAndStatusAndLifestylesAndKcalGreaterThanOrderByKcalAsc(
            Language language, Status status, Set<Lifestyle> lifestyles, Integer kcal, Pageable pageable
    );
    public Page<NutritionProgram> findAllByLanguageAndStatusAndLifestylesAndKcalLessThanEqualOrderByKcalDesc(
            Language language, Status status, Set<Lifestyle> lifestyles, Integer kcal, Pageable pageable
    );

    public Stream<NutritionProgram> findAllByLanguageAndStatusAndLifestylesAndKcalGreaterThanOrderByKcalAsc(
            Language language, Status status, Set<Lifestyle> lifestyles, Integer kcal
    );
    public Stream<NutritionProgram> findAllByLanguageAndStatusAndLifestylesAndKcalLessThanEqualOrderByKcalDesc(
            Language language, Status status, Set<Lifestyle> lifestyles, Integer kcal
    );

    public List<NutritionProgram> findAllByStatus(Status status);
    public List<NutritionProgram> findAllByStatusAndLifestyles(Status status, Set<Lifestyle> lifestyles);

    public Page<NutritionProgram> findAllByStatusAndLifestylesAndKcal(Status status, Set<Lifestyle> lifestyles, Integer kcal);

    public Long countAllByLanguage(Language language);

}

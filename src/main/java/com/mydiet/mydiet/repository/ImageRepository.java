package com.mydiet.mydiet.repository;

import com.mydiet.mydiet.domain.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, Long> {

    public Optional<Image> findImageByName(String name);

}

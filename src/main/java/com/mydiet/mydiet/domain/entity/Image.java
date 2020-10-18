package com.mydiet.mydiet.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = READ_ONLY)
    private Long id;

    private String name;
    private String resource; // base64 encoding

}

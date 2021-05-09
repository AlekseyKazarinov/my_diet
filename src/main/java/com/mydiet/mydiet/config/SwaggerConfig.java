package com.mydiet.mydiet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
public class SwaggerConfig {

    public static final String APP_CONTROLLER_TAG = "Android application functionality";

    @Bean
    public Docket api() {
        var groupName = "Swagger";

        // should update some parts of config according to https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mydiet.mydiet.controller"))
                .paths(PathSelectors.any())
                .build()
                .tags(new Tag(APP_CONTROLLER_TAG, "Provides operations in application-compatible format (for Android users only)"))
                .groupName(groupName)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "MyDiet Server REST API documentation",
                "The API is aimed to provide docs for server-side requests from external users (including Android App users and admin users)\n" +
                        "The REST API is divided into 2 blocks. First one is dedicated for serving Android App users whereas the second one is " +
                        "for operating server data by admins",
                "API 0.1",  // this value should be changed every time when API is adjusted
                "Terms of service",
                new Contact("Alexey Kazarinov", "https://www.vk.com/aleksey_kazarinov", "aleksey.kaz@mail.ru"),
                ApiInfo.DEFAULT.getLicense(), ApiInfo.DEFAULT.getLicenseUrl(), ApiInfo.DEFAULT.getVendorExtensions());
    }

}

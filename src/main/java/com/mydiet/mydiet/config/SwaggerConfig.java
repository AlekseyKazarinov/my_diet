package com.mydiet.mydiet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String APP_CONTROLLER_TAG = "Android application functionality";

    /**
     * Настраивает группу API и указывает, какие пакеты сканировать
     * (аналог .select().apis(...).paths(...) из Springfox).
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Swagger")
                .packagesToScan("com.mydiet.mydiet.controller")
                .build();
    }

    /**
     * Настраивает метаданные API (название, описание, контакты, теги).
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MyDiet Server REST API documentation")
                        .description("The API is aimed to provide docs for server-side requests from external users (including Android App users and admin users)\n" +
                                "The REST API is divided into 2 blocks. First one is dedicated for serving Android App users whereas the second one is " +
                                "for operating server data by admins")
                        .version("API 0.1")  // this value should be changed every time when API is adjusted
                        .termsOfService("Terms of service")
                        .contact(new Contact()
                                .name("Alexey Kazarinov")
                                .url("https://www.vk.com/aleksey_kazarinov")
                                .email("aleksey.kaz@mail.ru"))
                        .license(new License()
                                .name("Apache 2.0") // В Springfox это было ApiInfo.DEFAULT.getLicense()
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .addTagsItem(new Tag()
                        .name(APP_CONTROLLER_TAG)
                        .description("Provides operations in application-compatible format (for Android users only)"));
    }
}

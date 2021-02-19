package com.mydiet.mydiet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigValidationRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // todo: validate configs in the future

        log.info("Config settings are valid");
    }
}

package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.repository.ConversionUnitsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ConversionUnitsServiceTest {


    @MockitoBean
    ConversionUnitsRepository conversionUnitsRepository;

    @Autowired
    @InjectMocks
    private ConversionUnitsService conversionUnitsService;

    @Test
    public void getCoefficientForTest() {

    }


}
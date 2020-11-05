package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.QuantityUnit;
import com.mydiet.mydiet.domain.exception.GenericException;
import com.mydiet.mydiet.repository.ConversionUnitsRepository;
import com.mydiet.mydiet.service.ProductService;
import com.mydiet.mydiet.service.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mydiet.mydiet.domain.entity.QuantityUnit.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionUnitsService {

    private static final Set<QuantityUnit> CONVERTIBLE_UNITS = Set.of(
            TEASPOON, TABLESPOON, GLASS, CUP, PINCH, PIECE, DROP
    );

    private final ConversionUnitsRepository conversionUnitsRepository;
    private final ProductService productService;

    public Double getCoefficientFor(QuantityUnit initUnit, Long productId) {
        if (!CONVERTIBLE_UNITS.contains(initUnit)) {
            return getPredefinedCoefficientFor(initUnit);
        }

        var convCoefList = conversionUnitsRepository.findConversionCoefficients(initUnit.name().toLowerCase(), productId);

        if (convCoefList.isEmpty()) {
            var message = String.format("Failed to convert from %s to next unit for Product %s. " +
                    "Conversion coefficient does not exist", initUnit, productId);
            log.error("Conversiont error: {}", message);

            throw new GenericException(message);
        }
        log.debug("conversion coefficient for {} is {}", initUnit, convCoefList.get(0));

        return convCoefList.get(0);
    }

    private Double getPredefinedCoefficientFor(QuantityUnit initUnit) {
        switch (initUnit) {
            case GRAM:
            case MILLILITER:
                return 0.001;
            case HEAPED_TABLESPOON:
            case HEAPED_TEASPOON:
                return 1.5;
            case KILOGRAM:
            case LITER:
                return 1.0;
            default:
                throw new IllegalArgumentException("Non supported initUnit " +
                        initUnit.name() + ": not existing predefined coefficient");
        }
    }

    public List<String> getAllAvailableUnitsForProduct(Long productId) {
        var product = productService.getProductOrThrow(productId);

        return QuantityUnit.getUnitsForConsistence(product.getConsistence()).stream()
                .map(unit -> unit.name().toLowerCase())
                .collect(Collectors.toList());
    }

    public List<String> getConvertibleUnitsForProduct(Long productId) {
        var product = productService.getProductOrThrow(productId);

        return CONVERTIBLE_UNITS.stream()
                .filter(unit -> unit.getConsistences().contains(product.getConsistence()))
                .map(unit -> unit.name().toLowerCase())
                .collect(Collectors.toList());
    }

    public ConversionUnits updateConvCoefficientsForProduct(Long productId, ConversionUnits conversionUnitsUpdate) {
        var product = productService.getProductOrThrow(productId);
        validateConversionUnits(conversionUnitsUpdate);

        var optionalConversionUnits = conversionUnitsRepository.findByProductId(productId);
        if (optionalConversionUnits.isEmpty()) {
            conversionUnitsUpdate.setProduct(product);
            return conversionUnitsRepository.save(conversionUnitsUpdate);

        } else {
            var convUnits = optionalConversionUnits.get();
            updateByNonNullFields(convUnits, conversionUnitsUpdate);
            return conversionUnitsRepository.save(convUnits);
        }
    }

    private void updateByNonNullFields(ConversionUnits initConversionUnits, ConversionUnits conversionUnitsUpdate) {
        Optional.ofNullable(conversionUnitsUpdate.getPinch()).ifPresent(initConversionUnits::setPinch);
        Optional.ofNullable(conversionUnitsUpdate.getPiece()).ifPresent(initConversionUnits::setPiece);
        Optional.ofNullable(conversionUnitsUpdate.getCup()).ifPresent(initConversionUnits::setCup);
        Optional.ofNullable(conversionUnitsUpdate.getGlass()).ifPresent(initConversionUnits::setGlass);
        Optional.ofNullable(conversionUnitsUpdate.getDrop()).ifPresent(initConversionUnits::setDrop);
        Optional.ofNullable(conversionUnitsUpdate.getTablespoon()).ifPresent(initConversionUnits::setTablespoon);
        Optional.ofNullable(conversionUnitsUpdate.getTeaspoon()).ifPresent(initConversionUnits::setTeaspoon);
    }

    public ConversionUnits createConvCoefficientsForProduct(Long productId, ConversionUnits conversionUnits) {
        var product = productService.getProductOrThrow(productId);
        validateConversionUnits(conversionUnits);

        conversionUnits.setProduct(product);

        return conversionUnitsRepository.save(conversionUnits);
    }


    private void validateConversionUnits(ConversionUnits conversionUnits) {
        Utils.validateEntityValueIsNonNegative(conversionUnits.getTeaspoon(), "teaspoon", conversionUnits);
        Utils.validateEntityValueIsNonNegative(conversionUnits.getTablespoon(), "tablespoon", conversionUnits);
        Utils.validateEntityValueIsNonNegative(conversionUnits.getCup(), "cup", conversionUnits);
        Utils.validateEntityValueIsNonNegative(conversionUnits.getDrop(), "drop", conversionUnits);
        Utils.validateEntityValueIsNonNegative(conversionUnits.getGlass(), "glass", conversionUnits);
        Utils.validateEntityValueIsNonNegative(conversionUnits.getPiece(), "piece", conversionUnits);
        Utils.validateEntityValueIsNonNegative(conversionUnits.getPinch(), "pinch", conversionUnits);
    }


}

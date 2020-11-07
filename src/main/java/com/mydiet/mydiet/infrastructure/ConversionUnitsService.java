package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.dto.input.ConversionUnitsInput;
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

    private Double getCoeffitientByName(ConversionUnits conversionUnits, QuantityUnit unit) {
        switch (unit) {
            case CUP: return conversionUnits.getCup();
            case DROP: return conversionUnits.getDrop();
            case TABLESPOON: return conversionUnits.getTablespoon();
            case TEASPOON: return conversionUnits.getTeaspoon();
            case GLASS: return conversionUnits.getGlass();
            case PINCH: return conversionUnits.getPinch();
            case PIECE: return conversionUnits.getPiece();
            default:
                throw new IllegalArgumentException(String.format("Not supported unit to convert: %s", unit));
        }
    }

    public Double getCoefficientFor(QuantityUnit initUnit, Long productId) {
        if (!CONVERTIBLE_UNITS.contains(initUnit)) {
            return getPredefinedCoefficientFor(initUnit);
        }

        //var optionalConvCoef = conversionUnitsRepository.findConversionCoefficients(initUnit.name(), productId);
        var convCoefsForUnits = conversionUnitsRepository.findByProductId(productId);

        if (convCoefsForUnits.isEmpty()) {
            var message = String.format("Failed to convert from %s to next unit for Product %s. " +
                    "Conversion coefficient does not exist", initUnit, productId);
            log.error("Conversiont error: {}", message);

            throw new GenericException(message);
        }
        var coef = getCoeffitientByName(convCoefsForUnits.get(), initUnit);

        log.info("conversion coefficient for {} is {}", initUnit, coef);

        return coef;
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

    public ConversionUnits updateConvCoefficientsForProduct(Long productId,
                                                            ConversionUnitsInput conversionUnitsUpdateInput) {
        var product = productService.getProductOrThrow(productId);
        validateConversionUnitsInput(conversionUnitsUpdateInput);

        var optionalConversionUnits = conversionUnitsRepository.findByProductId(productId);
        if (optionalConversionUnits.isEmpty()) {
            return createConvCoefficientsForProduct(productId, conversionUnitsUpdateInput);

        } else {
            var convUnits = optionalConversionUnits.get();
            updateByNonNullFields(convUnits, convUnits);
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

    public ConversionUnits createConvCoefficientsForProduct(Long productId, ConversionUnitsInput conversionUnitsInput) {
        var product = productService.getProductOrThrow(productId);
        validateConversionUnitsInput(conversionUnitsInput);

        var convUnits = ConversionUnits.builder()
                .cup(conversionUnitsInput.getCup())
                .drop(conversionUnitsInput.getDrop())
                .glass(conversionUnitsInput.getGlass())
                .piece(conversionUnitsInput.getPiece())
                .pinch(conversionUnitsInput.getPinch())
                .tablespoon(conversionUnitsInput.getTablespoon())
                .teaspoon(conversionUnitsInput.getTeaspoon())
                .product(product)
                .build();

        return conversionUnitsRepository.save(convUnits);
    }


    private void validateConversionUnitsInput(ConversionUnitsInput convUnitsInput) {
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getTeaspoon(), "teaspoon", convUnitsInput);
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getTablespoon(), "tablespoon", convUnitsInput);
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getCup(), "cup", convUnitsInput);
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getDrop(), "drop", convUnitsInput);
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getGlass(), "glass", convUnitsInput);
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getPiece(), "piece", convUnitsInput);
        Utils.validateFieldIsNonNegativeIfExists(convUnitsInput.getPinch(), "pinch", convUnitsInput);
    }


}

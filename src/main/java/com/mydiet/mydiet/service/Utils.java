package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.exception.ValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class Utils {

    public static void validateFieldIsSet(String field, Object entity) {
        if (StringUtils.isEmpty(field)) {
            var message = String.format("Field %s should be set for %s", field, name(entity));
            throw new ValidationException(message);
        }
    }

    public static void validateValueIsSet(Number value, Object entity) {
        if (value == null) {
            var message = String.format("Value %s should be set for creating Recipe", value, name(entity));
            throw new ValidationException(message);
        }
    }

    public static void validateValueIsNonNegative(Double value, Object entity) {
        validateValueIsSet(value, entity);
        if (value < 0) {
            var message = String.format("Value %s should be positive or equal to zero", value, name(entity));
        }
    }

    private static String name(Object entity) {
        return entity.getClass().getSimpleName();
    }
}

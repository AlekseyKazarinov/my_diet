package com.mydiet.mydiet.service;

import com.mydiet.mydiet.domain.exception.ValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.Collection;

@UtilityClass
public class Utils {

    private static final String VALUE_NON_NEGATIVE = "Value %s should be positive or equal to zero";
    private static final String FIELD_NON_NEGATIVE = VALUE_NON_NEGATIVE + " for %s";
    private static final String FIELD_NON_NULL = "Field %s should be set for %s";
    private static final String TEXT_FIELD_NON_NULL = "Field %s should be set";
    private static final String VALUE_NON_NULL = "Value %s should be set";

    public static void validateTextFieldIsSet(String fieldValue, String fieldName, Object entity) {
        if (StringUtils.isEmpty(fieldValue)) {
            var message = String.format(FIELD_NON_NULL, fieldName, name(entity));
            throw new ValidationException(message);
        }
    }

    public static void validateTextVariableIsSet(String field, String variableName) {
        if (StringUtils.isEmpty(field)) {
            var message = String.format(TEXT_FIELD_NON_NULL, variableName);
            throw new ValidationException(message);
        }
    }

    public static void validateFieldIsSet(Object value, String fieldName, Object entity) {
        if (value == null) {
            var message = String.format("Value %s should be set for creating %s", fieldName, name(entity));
            throw new ValidationException(message);
        }
    }

    public static void validateFieldIsNonNegative(Number value, String fieldName, Object entity) {
        validateFieldIsSet(value, fieldName, entity);
        if (value.doubleValue() < 0) {
            var message = String.format(FIELD_NON_NEGATIVE, fieldName, name(entity));
            throw new ValidationException(message);
        }
    }

    public static void validateFieldIsNonNegativeIfExists(Number value, String fieldName, Object entity) {
        if (value != null) {
            validateFieldIsNonNegative(value, fieldName, entity);
        }
    }

    public static void validateVariableIsNonNegative(Number value, String valueName) {
        if (value == null) {
            var message = String.format(VALUE_NON_NULL, valueName);
            throw new ValidationException(message);
        }

        if (value.doubleValue() < 0) {
            var message = String.format(VALUE_NON_NEGATIVE, valueName);
            throw new ValidationException(message);
        }
    }

    public static void validateCollectionContainsElements(Collection<? extends Object> collection,
                                                          String listName,
                                                          Object entity) {
        if (collection == null) {
            var message = String.format("List '%s' for %s should not be null", listName, name(entity));
            throw new ValidationException(message);
        }

        if (collection.isEmpty()) {
            var message = String.format("List '%s' for %s should not be empty", listName, name(entity));
            throw new ValidationException(message);
        }
    }

    private static String name(Object entity) {
        return entity.getClass().getSimpleName();
    }
}

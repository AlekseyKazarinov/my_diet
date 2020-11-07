package com.mydiet.mydiet.infrastructure;

import com.mydiet.mydiet.domain.entity.ProductType;
import com.mydiet.mydiet.domain.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public enum Consistence {

    LIQUID("Жидкий"),       // milk, water, ...
    SOLID("Твёрдый"),        // apple, bread, ...
    NOT_DEFINED("Не определено");  // greens, ...

    @Getter
    private final String description;

    private static final Set<String> consistenceNames = Arrays.stream(Consistence.values())
            .flatMap(c -> Stream.of(c.name(), c.description))
            .collect(Collectors.toSet());

    public static Consistence of(String name) {
        if (name == null) {
            return Consistence.NOT_DEFINED;
        }

        for (var c : Consistence.values()) {
            if (c.name().equalsIgnoreCase(name)
                    || c.description.equalsIgnoreCase(name)) {
                return c;
            }
        }

        throw new IllegalArgumentException(String.format("Consistence with name: %s does not exist", name));
    }

    private static boolean isValidConsistence(String name) {
        return consistenceNames.contains(name);
    }

    public static void validateConsistence(String name) {
        if (!isValidConsistence(name)) {
            var message = String.format("%s is not a valid Consistence", name);

            throw new ValidationException(message);
        }
    }

}

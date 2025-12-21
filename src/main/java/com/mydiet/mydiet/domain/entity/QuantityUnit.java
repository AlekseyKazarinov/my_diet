package com.mydiet.mydiet.domain.entity;

import com.mydiet.mydiet.domain.exception.GenericException;
import com.mydiet.mydiet.domain.exception.ValidationException;
import com.mydiet.mydiet.infrastructure.Consistence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static com.mydiet.mydiet.infrastructure.Consistence.*;

@AllArgsConstructor
public enum QuantityUnit {

    KILOGRAM            (Set.of(SOLID)),
    GRAM                (Set.of(SOLID)),
    MILLILITER          (Set.of(SOLID)),
    LITER               (Set.of(LIQUID)),
    TEASPOON            (Set.of(SOLID, LIQUID)),
    HEAPED_TEASPOON     (Set.of(SOLID)),
    TABLESPOON          (Set.of(SOLID, LIQUID)),
    HEAPED_TABLESPOON   (Set.of(SOLID)),
    GLASS               (Set.of(SOLID, LIQUID)),
    CUP                 (Set.of(LIQUID)),
    PINCH               (Set.of(SOLID)),
    PIECE               (Set.of(SOLID)),
    DROP                (Set.of(LIQUID)),
    BY_TASTE            (Set.of(NOT_DEFINED)),
    OPTIONAL            (Set.of(NOT_DEFINED)),
    NOT_USED            (Set.of(NOT_DEFINED));

    @Getter
    private final Set<Consistence> consistences;

    private static final String NOT_EXIST = "Quantity Unit %s does not exist";

    public String name(Language language) {
        return retrieveTranslationFor(this, language).getFirst();
    }

    public String shortName(Language language) {
        return retrieveTranslationFor(this, language).getSecond();
    }

    private static Pair<String, String> retrieveTranslationFor(QuantityUnit unit, Language language) {
        var languageMapper = mapper.get(language);

        if (languageMapper == null) {
            throw new GenericException("Language mapper not defined for " + language);
        }

        var translations = languageMapper.get(unit);

        if (translations == null) {
            throw new GenericException(String.format("Translation is not provided for unit %s in %s language", unit, language));
        }

        return translations;
    }

    private static final Map<Language, Map<QuantityUnit, Pair<String, String>>> mapper = generateTranslationTable();

    private static Map<Language, Map<QuantityUnit, Pair<String, String>>> generateTranslationTable() {
        var mapper = new HashMap<Language, Map<QuantityUnit, Pair<String, String>>>();

        // RU
        var russianMapper = new HashMap<QuantityUnit, Pair<String, String>>();

        russianMapper.put(  KILOGRAM,             Pair.of(  "килограмм",        "кг."           ));
        russianMapper.put(  GRAM,                 Pair.of(  "грамм",            "г."            ));
        russianMapper.put(  MILLILITER,           Pair.of(  "миллилитр",        "мл."           ));
        russianMapper.put(  LITER,                Pair.of(  "литр",             "л"             ));
        russianMapper.put(  TEASPOON,             Pair.of(  "чайная лолжка",    "ч.л."          ));
        russianMapper.put(  HEAPED_TEASPOON,      Pair.of(  "чайная ложка с горкой","ч.л. с горкой"));
        russianMapper.put(  TABLESPOON,           Pair.of(  "столовая ложка",   "ст.л."         ));
        russianMapper.put(  HEAPED_TABLESPOON,    Pair.of(  "столовая ложка с горкой","ст.л. с горкой"));
        russianMapper.put(  GLASS,                Pair.of(  "стакан",           "ст."           ));
        russianMapper.put(  CUP,                  Pair.of(  "чашка",            "чашка"         ));
        russianMapper.put(  PINCH,                Pair.of(  "щепотка",          "щеп."          ));
        russianMapper.put(  PIECE,                Pair.of(  "штука",            "шт."           ));
        russianMapper.put(  DROP,                 Pair.of(  "капля",            "капля"         ));
        russianMapper.put(  BY_TASTE,             Pair.of(  "по вкусу",         "по вкусу"      ));
        russianMapper.put(  OPTIONAL,             Pair.of(  "по желанию",       "по желанию"    ));
        russianMapper.put(  NOT_USED,             Pair.of(  "null",             "null"          ));

        mapper.put(Language.RUSSIAN, russianMapper);

        // EN
        var englishMapper = new HashMap<QuantityUnit, Pair<String, String>>();

        englishMapper.put(  KILOGRAM,           Pair.of(    "kilogram",         "kg."           ));
        englishMapper.put(  GRAM,               Pair.of(    "gram",             "g."            ));
        englishMapper.put(  MILLILITER,         Pair.of(    "milliliter",       "ml."           ));
        englishMapper.put(  LITER,              Pair.of(    "liter",            "L."            ));
        englishMapper.put(  TEASPOON,           Pair.of(    "teaspoon",         "tsp."          ));
        englishMapper.put(  HEAPED_TEASPOON,    Pair.of(    "heaped teaspoon",  "heaped tsp."   ));
        englishMapper.put(  TABLESPOON,         Pair.of(    "tablespoon",       "tbsp."         ));
        englishMapper.put(  HEAPED_TABLESPOON,  Pair.of(    "heaped tablespoon","heaped tbsp."  ));
        englishMapper.put(  GLASS,              Pair.of(    "glass",            "glass"         ));
        englishMapper.put(  CUP,                Pair.of(    "cup",              "cup"           ));
        englishMapper.put(  PINCH,              Pair.of(    "pinch",            "pinch"         ));
        englishMapper.put(  PIECE,              Pair.of(    "piece",            "piece"         ));
        englishMapper.put(  DROP,               Pair.of(    "drop",             "drop"          ));
        englishMapper.put(  BY_TASTE,           Pair.of(    "by taste",         "by taste"      ));
        englishMapper.put(  OPTIONAL,           Pair.of(    "optional",         "optional"      ));
        englishMapper.put(  NOT_USED,           Pair.of(    "null",             "null"          ));

        mapper.put(Language.ENGLISH, englishMapper);

        return mapper;
    }

    private static Set<String> unitNames = Arrays.stream(QuantityUnit.values())
            .map(QuantityUnit::name)
            .collect(Collectors.toSet());

    // todo: use custom Json(De)Serializer to convert from string and back
    public static QuantityUnit of(String unit) {
        for (var quantityUnit : QuantityUnit.values()) {
            if (quantityUnit.name().equalsIgnoreCase(unit)) {
                return quantityUnit;
            }
        }

        throw new IllegalArgumentException(String.format(NOT_EXIST, unit));
    }

    private static boolean isValidUnit(String name) {
        return unitNames.contains(name);
    }

    public static void validateUnit(String name) {
        if (!isValidUnit(name)) {
            var message = String.format("%s is not a valid QuantityUnit", name);
            throw new ValidationException(message);
        }
    }

    public static Set<QuantityUnit> getUnitsForConsistence(Consistence consistence) {
        return Arrays.stream(QuantityUnit.values())
                .filter(unit -> unit.consistences.contains(consistence))
                .collect(Collectors.toUnmodifiableSet());
    }

}

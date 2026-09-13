package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Language {

    RUSSIAN("RU"),  // default, language null means RUSSIAN
    ENGLISH("EN");

    private final String code;

    public static boolean isRussian(Language language){
        return language == null || language == RUSSIAN;
    }

    public static boolean areEqual(Language leftLanguage, Language rightLanguage) {
        if ((leftLanguage == null && isRussian(rightLanguage))
            || (isRussian(leftLanguage) && rightLanguage == null))
            return true;

        return leftLanguage == rightLanguage;
    }

    public static String print(Language language) {
        return isRussian(language) ? RUSSIAN.toString() : language.toString();
    }

}

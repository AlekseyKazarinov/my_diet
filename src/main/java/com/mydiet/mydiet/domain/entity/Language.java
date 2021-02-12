package com.mydiet.mydiet.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Language {

    RUSSIAN("RU"),  // default
    ENGLISH("EN");

    private final String code;

    public static boolean isRussian(Language language){
        return language == null || language == RUSSIAN;
    }

    public static boolean areEqual(Language leftLanguage, Language rightLanguage) {
        if (Language.isRussian(leftLanguage) && Language.isRussian(rightLanguage)) {
            return true;
        }

        return leftLanguage == rightLanguage;
    }

}

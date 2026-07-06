package com.dvid.dcam.feature.settings.domain;

import java.util.Locale;

/** Language choices supported by the DCAM app shell. */
public enum AppLanguage {
    SYSTEM(""),
    ENGLISH("en"),
    VIETNAMESE("vi");

    private final String languageTag;

    AppLanguage(String languageTag) {
        this.languageTag = languageTag;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public boolean isSystemDefault() {
        return languageTag.isEmpty();
    }

    public Locale toLocale() {
        return isSystemDefault() ? Locale.getDefault() : Locale.forLanguageTag(languageTag);
    }

    public static AppLanguage fromLanguageTag(String languageTag) {
        if (languageTag == null || languageTag.isBlank()) return SYSTEM;
        for (AppLanguage language : values()) {
            if (language.languageTag.equalsIgnoreCase(languageTag)) return language;
        }
        return SYSTEM;
    }
}


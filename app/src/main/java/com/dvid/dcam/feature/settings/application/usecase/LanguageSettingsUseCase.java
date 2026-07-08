package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.domain.AppLanguage;

/** Application entry point for app language settings. */
public interface LanguageSettingsUseCase {
    AppLanguage currentLanguage();
    AppLanguage[] supportedLanguages();
    void changeLanguage(AppLanguage language);
}


package com.dvid.dcam.feature.settings.application.usecase;

import com.dvid.dcam.feature.settings.application.port.LanguagePreferenceStore;
import com.dvid.dcam.feature.settings.domain.AppLanguage;

public final class LanguageSettingsUseCaseImpl implements LanguageSettingsUseCase {
    private final LanguagePreferenceStore languagePreferences;

    public LanguageSettingsUseCaseImpl(LanguagePreferenceStore languagePreferences) {
        this.languagePreferences = languagePreferences;
    }

    @Override public AppLanguage currentLanguage() {
        return languagePreferences.currentLanguage();
    }

    @Override public AppLanguage[] supportedLanguages() {
        return AppLanguage.values();
    }

    @Override public void changeLanguage(AppLanguage language) {
        languagePreferences.selectLanguage(language == null ? AppLanguage.SYSTEM : language);
    }
}

package com.dvid.dcam.feature.settings.application.port;

import com.dvid.dcam.feature.settings.domain.AppLanguage;

/** Persistence and platform boundary for app-language preference. */
public interface LanguagePreferenceStore {
    AppLanguage currentLanguage();
    void selectLanguage(AppLanguage language);
}

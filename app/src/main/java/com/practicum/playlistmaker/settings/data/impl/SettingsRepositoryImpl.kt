package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.data.api.SettingsStorageApi
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(private val storageApi : SettingsStorageApi) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return storageApi.readSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storageApi.writeSettings(settings)
    }
}
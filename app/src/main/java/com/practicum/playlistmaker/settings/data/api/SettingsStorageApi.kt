package com.practicum.playlistmaker.settings.data.api

import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsStorageApi {
    fun readSettings() : ThemeSettings
    fun writeSettings(settings: ThemeSettings)
}
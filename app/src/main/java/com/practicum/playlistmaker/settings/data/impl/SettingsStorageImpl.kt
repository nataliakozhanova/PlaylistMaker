package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.data.api.SettingsStorageApi
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings

const val THEME_KEY = "key_for_app_theme"

class SettingsStorageImpl(private val sharedPreferences: SharedPreferences) : SettingsStorageApi {
    override fun readSettings(): ThemeSettings {
        val settings = ThemeSettings(false,false)
        if (!sharedPreferences.contains(THEME_KEY)) {
            settings.isEmpty = true

        } else {
            settings.isEmpty = false
            settings.darkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        }
        return settings
    }

    override fun writeSettings(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, settings.darkTheme)
            .apply()
    }
}
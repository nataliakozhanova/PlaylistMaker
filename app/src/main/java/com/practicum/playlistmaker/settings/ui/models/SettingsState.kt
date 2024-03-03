package com.practicum.playlistmaker.settings.ui.models

sealed interface SettingsState {
    data class DarkMode(
        val isDark : Boolean
    ) : SettingsState
}
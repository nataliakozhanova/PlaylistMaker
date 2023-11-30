package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        setTheme(getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    fun setTheme(sharedPrefs: SharedPreferences) {
        if (sharedPrefs.contains(THEME_KEY)) {
            (applicationContext as App).switchTheme(sharedPrefs.getBoolean(THEME_KEY, isSystemDarkMode()))
        } else {
            switchTheme(isSystemDarkMode())
        }
    }
    fun isSystemDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }
}
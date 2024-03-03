package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.models.ThemeSettings
import com.practicum.playlistmaker.settings.ui.models.SettingsState
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.App

class SettingsViewModel(
    application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(sharingInteractor: SharingInteractor, settingsInteractor: SettingsInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application, sharingInteractor, settingsInteractor)
            }
        }
    }
    private val settingsState : SettingsState

    init {
        val themeSettings = settingsInteractor.getThemeSettings()
        if (themeSettings.isEmpty) {
            settingsState = SettingsState.DarkMode((application as App).isSystemDarkMode())

        } else {
            settingsState = SettingsState.DarkMode(themeSettings.darkTheme)
        }
    }

    fun getState(): SettingsState = settingsState

    fun changeSettings(isDark : Boolean) {
        (getApplication<Application>() as App).switchTheme(isDark)
        settingsInteractor.updateThemeSetting(ThemeSettings(isDark, false))
    }
    fun getSharingIntent() : Intent {
        return sharingInteractor.shareApp()
    }
    fun getWriteToSupportIntent() : Intent {
        return sharingInteractor.openSupport()
    }
    fun getTermsIntent() : Intent {
        return sharingInteractor.openTerms()
    }
}
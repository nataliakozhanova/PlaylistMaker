package com.practicum.playlistmaker.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.player.data.PlayerApiImpl
import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.history.data.storage.HistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repo.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.SearchApiImpl
import com.practicum.playlistmaker.history.data.storage.HistoryStorageImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.history.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.history.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.data.impl.SettingsStorageImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

const val THEME_PREFERENCES = "playlist_maker_preferences"
const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"

object Creator {
    private fun getSearchApi(): SearchApi {
        return SearchApiImpl()
    }

    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(this.getSearchApi())
    }

    fun getSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
    }

    private fun getPlayerRepository(): PlayerApi {
        return PlayerApiImpl()
    }

    fun getPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(this.getPlayerRepository())
    }

    fun getHistoryInteractor(context: Context): HistoryInteractor {
        return HistoryInteractorImpl(
            HistoryRepositoryImpl(
                HistoryStorageImpl(context.getSharedPreferences(
                    SEARCH_HISTORY_PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                ))
            )
        )
    }

    fun getSharingInteractor(
        shareLink: String,
        termsLink: String,
        email: String,
        subject: String,
        message: String
    ): SharingInteractor {
        return SharingInteractorImpl(
            ExternalNavigatorImpl(),
            shareLink, termsLink, EmailData(email, subject, message)
        )
    }

    fun getSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(
                SettingsStorageImpl(
                    context.getSharedPreferences(
                        THEME_PREFERENCES,
                        AppCompatActivity.MODE_PRIVATE
                    )
                )
            )
        )
    }

}
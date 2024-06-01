package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.favorites.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.history.domain.api.HistoryInteractor
import com.practicum.playlistmaker.history.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import org.koin.dsl.module

val interactorModule = module {
    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> { (linkApp: String, linkTerms: String, emailData: EmailData) ->
        SharingInteractorImpl(get(), linkApp, linkTerms, emailData)
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    factory<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    factory<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}
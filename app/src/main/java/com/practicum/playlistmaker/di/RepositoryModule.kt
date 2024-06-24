package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.repo.FavoritesRepositoryImpl
import com.practicum.playlistmaker.favorites.domain.db.FavoritesRepository
import com.practicum.playlistmaker.history.data.repo.HistoryRepositoryImpl
import com.practicum.playlistmaker.history.domain.api.HistoryRepository
import com.practicum.playlistmaker.playlist.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlist.data.repo.PlaylistRepositoryImpl
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository
import com.practicum.playlistmaker.search.data.repo.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    factory { TrackDbConverter() }

    factory { PlaylistDbConverter(get()) }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
}
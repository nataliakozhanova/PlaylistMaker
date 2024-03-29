package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.history.data.storage.HistoryRepositoryImpl
import com.practicum.playlistmaker.history.domain.api.HistoryRepository
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
        SearchRepositoryImpl(get())
    }
}
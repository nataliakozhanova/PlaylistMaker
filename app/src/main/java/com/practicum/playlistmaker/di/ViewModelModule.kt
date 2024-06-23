package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorites.presentation.view_model.LibraryFavoritesViewModel
import com.practicum.playlistmaker.playlist.presentation.view_model.LibraryPlaylistsViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.playlist.presentation.view_model.EditingPlaylistViewModel
import com.practicum.playlistmaker.playlist.presentation.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.playlist.presentation.view_model.PlaylistViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {(linkApp: String, linkTerms: String, email: String, subject: String, message: String) ->
        val sharingInteractor : SharingInteractor by inject { parametersOf(linkApp, linkTerms, EmailData(email, subject, message))  }
        SettingsViewModel(androidApplication(), sharingInteractor, get())
    }

    viewModel{
        PlayerViewModel(androidApplication(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(androidApplication(), get(), get())
    }

    viewModel {
        LibraryPlaylistsViewModel(get())
    }

    viewModel {
        LibraryFavoritesViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
    viewModel {(playlistId: Int) ->
        val sharingInteractor : SharingInteractor by inject { parametersOf("", "", EmailData("", "", ""))  }
        PlaylistViewModel(playlistId, get(), sharingInteractor)
    }

    viewModel{(playlistId: Int) ->
        EditingPlaylistViewModel(get(), playlistId)
    }
}
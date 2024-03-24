package com.practicum.playlistmaker.library.ui.models

sealed interface FavoritesState {
    object Empty : FavoritesState
}
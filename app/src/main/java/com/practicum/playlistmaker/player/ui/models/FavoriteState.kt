package com.practicum.playlistmaker.player.ui.models

sealed interface FavoriteState {
    object Default : FavoriteState
    data class IsFavorite(val isFavorite: Boolean) : FavoriteState
}
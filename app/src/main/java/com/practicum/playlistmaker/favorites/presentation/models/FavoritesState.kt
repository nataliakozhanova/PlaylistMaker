package com.practicum.playlistmaker.favorites.presentation.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Content(
        val tracks: List<Track>
    ) : FavoritesState
}
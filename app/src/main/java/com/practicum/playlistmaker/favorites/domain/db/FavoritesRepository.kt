package com.practicum.playlistmaker.favorites.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
    fun getAllFavorites() : Flow<List<Track>>
}
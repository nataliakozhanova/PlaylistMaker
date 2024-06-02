package com.practicum.playlistmaker.favorites.domain.impl

import com.practicum.playlistmaker.favorites.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.favorites.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository : FavoritesRepository) : FavoritesInteractor {
    override fun getAllFavorites(): Flow<List<Track>> {
        return favoritesRepository.getAllFavorites()
    }

    override suspend fun addTrackToFavorites(track: Track) {
        favoritesRepository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        favoritesRepository.deleteTrackFromFavorites(track)
    }
}
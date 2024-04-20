package com.practicum.playlistmaker.favorites.data.repo

import com.practicum.playlistmaker.favorites.data.converters.TrackDbConverter
import com.practicum.playlistmaker.favorites.data.db.AppTrackDatabase
import com.practicum.playlistmaker.favorites.data.db.TrackEntity
import com.practicum.playlistmaker.favorites.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appTrackDatabase: AppTrackDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = convertFromTrack(track)
        appTrackDatabase.trackDao().insertFavoriteTrack(trackEntity)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        val trackEntity = convertFromTrack(track)
        appTrackDatabase.trackDao().deleteFavoriteTrack(trackEntity.trackId)
    }

    override fun getAllFavorites(): Flow<List<Track>> = flow {
        val allFavorites = appTrackDatabase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(allFavorites))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { trackEntity -> trackDbConverter.map(trackEntity) }
    }
    private fun convertFromTrack(track: Track) : TrackEntity {
        return trackDbConverter.map(track)
    }
}
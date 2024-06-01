package com.practicum.playlistmaker.search.data.repo

import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val searchApi: SearchApi,
    private val appDatabase: AppDatabase
) : SearchRepository {
    override fun getTrackListByName(expression: String): Flow<List<Track>> = flow {
        val response = searchApi.searchTracks(expression)
        val tracks = response.results.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTime,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
        val favoritesIDs = checkFavorites()
        tracks.forEach{track ->
            track.isFavorite = favoritesIDs.contains(track.trackId)
        }
        emit(tracks)
    }

    private suspend fun checkFavorites(): List<Int> {
        return appDatabase.trackDao().getFavoriteTrackIDs()
    }

}
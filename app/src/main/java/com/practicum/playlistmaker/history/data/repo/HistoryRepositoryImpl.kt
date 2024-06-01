package com.practicum.playlistmaker.history.data.repo

import com.practicum.playlistmaker.favorites.data.db.AppDatabase
import com.practicum.playlistmaker.history.data.api.HistoryStorageApi
import com.practicum.playlistmaker.history.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val storageApi : HistoryStorageApi,
    private val appDatabase: AppDatabase
) :
    HistoryRepository {

    override fun getSearchHistory() : Flow<MutableList<Track>> = flow {
        val tracksSearchHistory: MutableList<Track> = ArrayList()
        val savedSearchHistory = storageApi.read()
        for(i in savedSearchHistory.indices) {
            tracksSearchHistory.add(savedSearchHistory[i])
        }
        val favoritesIDs = checkFavorites()
        tracksSearchHistory.forEach{track ->
            track.isFavorite = favoritesIDs.contains(track.trackId)
        }
        emit(tracksSearchHistory)
    }

    override fun saveSearchHistory(tracks: MutableList<Track>) {
        storageApi.write(tracks)
    }

    private suspend fun checkFavorites(): List<Int> {
        return appDatabase.trackDao().getFavoriteTrackIDs()
    }
}

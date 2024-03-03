package com.practicum.playlistmaker.history.data.storage

import com.practicum.playlistmaker.history.data.api.HistoryStorageApi
import com.practicum.playlistmaker.history.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class HistoryRepositoryImpl(private val storageApi : HistoryStorageApi) :
    HistoryRepository {

    override fun getSearchHistory() : MutableList<Track> {
        val tracksSearchHistory: MutableList<Track> = ArrayList()
        val savedSearchHistory = storageApi.read()
        for(i in savedSearchHistory.indices) {
            tracksSearchHistory.add(savedSearchHistory[i])
        }
        return tracksSearchHistory
    }

    override fun saveSearchHistory(tracks: MutableList<Track>) {
        storageApi.write(tracks)
    }
}
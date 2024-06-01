package com.practicum.playlistmaker.history.domain.impl

import com.practicum.playlistmaker.history.domain.api.HistoryInteractor
import com.practicum.playlistmaker.history.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class HistoryInteractorImpl(private val repository: HistoryRepository) :
    HistoryInteractor {

    private val tracksSearchHistory: MutableList<Track> = ArrayList()


    override fun addToSearchHistory(track: Track) {
        for (item in tracksSearchHistory) {
            if (item.trackId == track.trackId) {
                tracksSearchHistory.remove(item)
                break
            }
        }
        tracksSearchHistory.add(0, track)
        if (tracksSearchHistory.size > 10) tracksSearchHistory.removeAt(10)
    }

    override fun deleteSearchHistory() {
        tracksSearchHistory.clear()
    }

    override fun saveSearchHistory() {
        repository.saveSearchHistory(tracksSearchHistory)
    }

    override fun getSearchHistory(): Flow<MutableList<Track>> = flow {
        tracksSearchHistory.clear()
        repository.getSearchHistory().collect { history -> tracksSearchHistory.addAll(history) }
        emit(tracksSearchHistory)
    }
}
package com.practicum.playlistmaker.history.domain.impl

import com.practicum.playlistmaker.history.domain.api.HistoryInteractor
import com.practicum.playlistmaker.history.domain.api.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track


class HistoryInteractorImpl(private val repository: HistoryRepository) :
    HistoryInteractor {

    val tracksSearchHistory: MutableList<Track> = repository.getSearchHistory()


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

    override fun getSearchHistory(): MutableList<Track> {
        return tracksSearchHistory
    }
}
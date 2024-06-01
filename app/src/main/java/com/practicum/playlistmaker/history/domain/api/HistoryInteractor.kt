package com.practicum.playlistmaker.history.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryInteractor {
    fun getSearchHistory() : Flow<List<Track>>
    fun addToSearchHistory(track: Track)
    fun deleteSearchHistory()
    fun saveSearchHistory()

}
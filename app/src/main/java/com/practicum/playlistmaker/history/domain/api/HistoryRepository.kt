package com.practicum.playlistmaker.history.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getSearchHistory() : Flow<MutableList<Track>>
    fun saveSearchHistory(tracks: MutableList<Track>)
}
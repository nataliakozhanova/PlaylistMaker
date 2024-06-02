package com.practicum.playlistmaker.history.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun getSearchHistory() : MutableList<Track>
    fun saveSearchHistory(tracks: MutableList<Track>)
}
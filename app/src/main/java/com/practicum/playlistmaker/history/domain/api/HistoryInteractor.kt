package com.practicum.playlistmaker.history.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryInteractor {
    fun getSearchHistory() : MutableList<Track>
    fun addToSearchHistory(track: Track)
    fun deleteSearchHistory()
    fun saveSearchHistory()

}
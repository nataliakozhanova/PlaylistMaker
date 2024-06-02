package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun getTrackListByName(expression: String) : Flow<SearchResult>
    fun getTracksIDs(): Flow<List<Int>>
}
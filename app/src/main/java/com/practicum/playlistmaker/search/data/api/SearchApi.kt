package com.practicum.playlistmaker.search.data.api

import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse

interface SearchApi {
    suspend fun searchTracks(searchQuery: String): TracksSearchResponse
}
package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksSearchResponse

interface SearchApi {
    fun searchTracks(searchQuery: String): TracksSearchResponse
}
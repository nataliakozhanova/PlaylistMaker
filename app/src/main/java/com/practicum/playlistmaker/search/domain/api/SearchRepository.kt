package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchRepository {
    fun getTrackListByName(expression: String) : List<Track>
}
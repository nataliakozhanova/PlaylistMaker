package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SearchResult

interface TracksInteractor {
    fun getTrackListByName(expression: String, consumer: SearchResultConsumer)

    interface SearchResultConsumer {
        fun consume(searchResult: SearchResult)
    }
}
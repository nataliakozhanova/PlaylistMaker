package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.SearchResult

interface SearchInteractor {
    fun getTrackListByName(expression: String, consumer: SearchResultConsumer)

    interface SearchResultConsumer {
        fun consume(searchResult: SearchResult)
    }
}
package com.practicum.playlistmaker.search.data.repo

import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {
    override fun getTrackListByName(expression: String): List<Track> {
        val response = searchApi.searchTracks(expression)

        return response.results
    }


}
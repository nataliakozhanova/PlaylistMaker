package com.practicum.playlistmaker.search.data.repo

import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {
    override fun getTrackListByName(expression: String): Flow<List<Track>> = flow {
        val response = searchApi.searchTracks(expression)

        emit(response.results)
    }


}
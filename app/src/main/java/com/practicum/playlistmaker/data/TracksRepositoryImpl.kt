package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val searchApi: SearchApi) : TracksRepository {
    override fun getTrackListByName(expression: String): List<Track> {
        val response = searchApi.searchTracks(expression)

        return response.results
    }


}
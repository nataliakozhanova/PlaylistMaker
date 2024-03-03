package com.practicum.playlistmaker.history.data.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryStorageApi {
    fun read() : Array<Track>
    fun write(tracks: MutableList<Track>)
}
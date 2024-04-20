package com.practicum.playlistmaker.playlist.domain.api

import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

interface PlaylistInteractor {
    fun getAllPlaylists() : Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun addNewPlaylist(playlist: Playlist)
    fun saveCoverFile(inputStream: InputStream, externalDir: File): String
}
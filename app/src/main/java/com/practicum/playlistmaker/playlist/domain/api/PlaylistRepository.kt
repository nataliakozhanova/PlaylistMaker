package com.practicum.playlistmaker.playlist.domain.api

import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun saveCoverFile(inputStream: InputStream, externalDir: File): String
    suspend fun addTrackToPlaylist(track: Track)

}
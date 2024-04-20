package com.practicum.playlistmaker.playlist.domain.impl

import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) : Boolean{
        for(item in playlist.trackIDList){
            if(item == track.trackId.toString()){
                return true
            }
        }
        playlist.numberOfTracks +=1
        playlist.trackIDList.add(track.trackId.toString())
        playlistRepository.updatePlaylist(playlist)
        playlistRepository.addTrackToPlaylist(track)
        return false
    }

    override suspend fun addNewPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override fun saveCoverFile(inputStream: InputStream, externalDir: File): String {
        return playlistRepository.saveCoverFile(inputStream, externalDir)
    }
}
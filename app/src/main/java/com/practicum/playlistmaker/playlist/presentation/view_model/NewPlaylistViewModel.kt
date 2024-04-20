package com.practicum.playlistmaker.playlist.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private var coverUri: String = ""

    fun addNewPlaylist(playlistName: String, playlistDescription: String, inputStream: InputStream?, externalDir: File) {

        coverUri = if(inputStream != null){
            playlistInteractor.saveCoverFile(inputStream, externalDir)
        } else {
            ""
        }

        val playlist = Playlist(playlistName, playlistDescription, coverUri)
        viewModelScope.launch {
            playlistInteractor.addNewPlaylist(playlist)
        }
    }

}
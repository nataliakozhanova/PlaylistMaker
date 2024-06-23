package com.practicum.playlistmaker.playlist.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

open class NewPlaylistViewModel(protected val playlistInteractor: PlaylistInteractor) : ViewModel() {

    protected var coverUri: String = ""

    open fun savePlaylistInfo(playlistName: String, playlistDescription: String, inputStream: InputStream?, externalDir: File) {

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
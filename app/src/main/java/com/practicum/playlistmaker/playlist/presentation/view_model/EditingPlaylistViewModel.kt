package com.practicum.playlistmaker.playlist.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.playlist.presentation.models.PlaylistInfoState
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class EditingPlaylistViewModel(
    playlistInteractor: PlaylistInteractor,
    private val playlistId: Int,
) : NewPlaylistViewModel(
    playlistInteractor
) {
    private lateinit var playlistToEdit: Playlist

    private val stateLiveData = MutableLiveData<PlaylistInfoState>()
    fun observeState(): LiveData<PlaylistInfoState> = stateLiveData

    init {
        viewModelScope.launch {
            playlistToEdit = playlistInteractor.getSelectedPlaylist(playlistId)
            stateLiveData.postValue(
                PlaylistInfoState.Content(
                    playlistToEdit.playlistName,
                    playlistToEdit.playlistDescription,
                    playlistToEdit.coverUri
                )
            )
        }
    }

    override fun savePlaylistInfo(
        playlistName: String,
        playlistDescription: String,
        inputStream: InputStream?,
        externalDir: File,
    ) {
        coverUri = if (inputStream != null) {
            playlistInteractor.saveCoverFile(inputStream, externalDir)
        } else {
            playlistToEdit.coverUri
        }

        viewModelScope.launch {
            playlistInteractor.updatePlaylistInfo(
                playlistName,
                playlistDescription, coverUri, playlistId
            )
        }
    }

}
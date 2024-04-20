package com.practicum.playlistmaker.playlist.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.presentation.models.PlaylistsState
import kotlinx.coroutines.launch

class LibraryPlaylistsViewModel (private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderPlaylistsState(PlaylistsState.Empty)
                    } else {
                        renderPlaylistsState(PlaylistsState.Content(playlists))
                    }
                }
        }
    }

    private fun renderPlaylistsState(playlistsState: PlaylistsState) {
        stateLiveData.postValue(playlistsState)
    }

}
package com.practicum.playlistmaker.playlist.presentation.view_model

import android.content.Intent
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.playlist.presentation.models.SelectedPlaylistsState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.getTrackTimeInSeconds
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.getCountableTracks
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    lateinit var playlist: Playlist
    private val tracksInPlaylist: MutableList<Track> = mutableListOf()

    private val stateLiveData = MutableLiveData<SelectedPlaylistsState>()
    fun observeState(): LiveData<SelectedPlaylistsState> = stateLiveData

    init {
        viewModelScope.launch {
            playlist = playlistInteractor.getSelectedPlaylist(playlistId)
            if (playlist.trackIDList.isEmpty()) {
                stateLiveData.postValue(SelectedPlaylistsState.Content(playlist, 0, emptyList()))
                return@launch
            }
            renderPlaylistInfo(playlist)
        }
    }

    fun getCurrentPlaylist(): Playlist {
        return playlist
    }

    fun deleteTrackFromPlaylist(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(trackId, playlist)
            renderPlaylistInfo(playlist)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    private suspend fun renderPlaylistInfo(playlist: Playlist) {
        playlistInteractor.getTracksByIDs(playlist.trackIDList)
            .collect { tracks ->
                tracksInPlaylist.clear()
                if (tracks.isEmpty()) {
                    stateLiveData.postValue(
                        SelectedPlaylistsState.Content(
                            playlist,
                            0,
                            emptyList()
                        )
                    )
                } else {
                    tracksInPlaylist.addAll(tracks)
                    var duration = 0
                    for (track in tracks) {
                        duration += getTrackTimeInSeconds(track.trackTime)
                    }
                    stateLiveData.postValue(
                        SelectedPlaylistsState.Content(
                            playlist,
                            duration / 60,
                            tracks
                        )
                    )
                }
            }
    }

    fun renderFragment() {
        viewModelScope.launch {
            playlist = playlistInteractor.getSelectedPlaylist(playlistId)
            if (playlist.trackIDList.isEmpty()) {
                stateLiveData.postValue(SelectedPlaylistsState.Content(playlist, 0, emptyList()))
                return@launch
            }
            renderPlaylistInfo(playlist)
        }
    }

    fun getSharingPlaylistIntent(resources: Resources): Intent {
        val stringBuilder = StringBuilder()
        stringBuilder.append(playlist.playlistName + "\n")
        if (playlist.playlistDescription.isNotEmpty()) {
            stringBuilder.append(playlist.playlistDescription + "\n")
        }
        stringBuilder.append(getCountableTracks(playlist.numberOfTracks, resources) + "\n")
        var number = 0
        for (track in tracksInPlaylist) {
            number += 1
            stringBuilder.append(number.toString() + ". " + track.artistName + " - " + track.trackName + " (" + track.trackTime + ")\n")
        }
        val playlistToShare = stringBuilder.toString()
        return sharingInteractor.sharePlaylist(playlistToShare)
    }

    fun isHasTracks(): Boolean {
        return playlist.trackIDList.isNotEmpty()
    }

}
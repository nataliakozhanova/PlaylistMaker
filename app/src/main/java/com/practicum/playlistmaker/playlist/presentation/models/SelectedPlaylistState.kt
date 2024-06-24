package com.practicum.playlistmaker.playlist.presentation.models

import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SelectedPlaylistsState {

    data class Content(
        val playlist: Playlist,
        val duration: Int,
        val tracks: List<Track>
    ) : SelectedPlaylistsState

    object Empty : SelectedPlaylistsState
}

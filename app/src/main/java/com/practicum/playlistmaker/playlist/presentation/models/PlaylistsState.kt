package com.practicum.playlistmaker.playlist.presentation.models

import com.practicum.playlistmaker.playlist.domain.models.Playlist

sealed interface PlaylistsState {

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

    object Empty : PlaylistsState
}
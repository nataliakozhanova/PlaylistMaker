package com.practicum.playlistmaker.playlist.presentation.models

sealed interface  PlaylistInfoState {
    data class Content(
        val playlistName: String,
        val playlistDescription: String,
        val coverUri: String
    ) : PlaylistInfoState
}
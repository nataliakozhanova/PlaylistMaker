package com.practicum.playlistmaker.search.ui.models

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val tracks: List<TrackUI>
    ) : SearchState

    data class Error(
        val errorMessage: String
    ) : SearchState

    data class Empty(
        val emptyMessage: String
    ) : SearchState

}
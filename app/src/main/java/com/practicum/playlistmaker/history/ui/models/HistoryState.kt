package com.practicum.playlistmaker.history.ui.models

import com.practicum.playlistmaker.search.domain.models.Track


sealed interface HistoryState  {

    data class Content(
        val tracks: List<Track>
    ) : HistoryState

    object Empty : HistoryState

}
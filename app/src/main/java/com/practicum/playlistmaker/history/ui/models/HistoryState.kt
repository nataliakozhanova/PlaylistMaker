package com.practicum.playlistmaker.history.ui.models

import com.practicum.playlistmaker.search.ui.models.TrackUI

sealed interface HistoryState  {

    data class Content(
        val tracks: List<TrackUI>
    ) : HistoryState

    object Empty : HistoryState

}
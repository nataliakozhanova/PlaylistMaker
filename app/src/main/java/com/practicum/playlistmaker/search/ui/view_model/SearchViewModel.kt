package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.history.domain.api.HistoryInteractor
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchViewModel(
    application: Application,
    private val historyInteractor: HistoryInteractor,
    private val searchInteractor: SearchInteractor
) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }


    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY_MILLIS, viewModelScope, true) { changedText ->
            searchTracks(changedText)
        }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
    }

    fun searchByClick(searchText: String) {
        if (latestSearchText == searchText && stateLiveData.value !is SearchState.Error) {
            return
        }
        this.latestSearchText = searchText

        searchTracks(searchText)
    }

    private fun searchTracks(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                searchInteractor
                    .getTrackListByName(newSearchText)
                    .collect { tracks ->
                        processResult(tracks)
                    }
            }
        }
    }

    private fun processResult(searchResult: SearchResult) {
        val tracks = mutableListOf<Track>()
        if (searchResult.tracks != null) {
            tracks.addAll(searchResult.tracks)
        }

        when {
            searchResult.hasErrors -> {
                renderState(
                    SearchState.Error(
                        getApplication<Application>().getString(R.string.connection_problems),
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    SearchState.Empty(
                        getApplication<Application>().getString(R.string.nothing_found),
                    )
                )
            }

            else -> {
                renderState(
                    SearchState.Content(
                        tracks = tracks,
                    )
                )
            }
        }

    }

    fun removeSearchedText() {
        renderState(SearchState.Content(tracks = mutableListOf()))
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun getHistoryState(): HistoryState {
        val historyTracks = historyInteractor.getSearchHistory()
        return if (historyTracks.isEmpty()) {
            HistoryState.Empty
        } else {
            HistoryState.Content(historyTracks)
        }
    }

    fun addToHistory(track: Track) {
        return historyInteractor.addToSearchHistory(track)
    }

    fun deleteHistory() {
        return historyInteractor.deleteSearchHistory()
    }

    fun saveHistory() {
        return historyInteractor.saveSearchHistory()
    }

    fun checkFavorites(track: Track) = runBlocking {
        val idsReceived = searchInteractor.getTracksIDs().toList()
        if (idsReceived.isEmpty()) {
            return@runBlocking
        }

        for (trackId in idsReceived.first()) {
            if (track.trackId == trackId) {
                track.isFavorite = true
                return@runBlocking
            }
        }
        track.isFavorite = false
    }
}
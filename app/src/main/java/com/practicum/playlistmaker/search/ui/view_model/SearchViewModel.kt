package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.history.domain.api.HistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.search.ui.models.TrackUI

class SearchViewModel(
    application: Application,
    private val historyInteractor : HistoryInteractor,
    private val searchInteractor: SearchInteractor) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTracks(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_MILLIS
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchByClick(searchText: String) {
        if (latestSearchText == searchText && stateLiveData.value !is SearchState.Error) {
            return
        }
        this.latestSearchText = searchText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTracks(searchText) }

        handler.post(searchRunnable)
    }

    private fun searchTracks(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            searchInteractor.getTrackListByName(
                newSearchText,
                object : SearchInteractor.SearchResultConsumer {
                    override fun consume(searchResult: SearchResult) {
                        val tracks = mutableListOf<TrackUI>()
                        if (searchResult.tracks != null) {
                            tracks.addAll(searchResult.tracks.map {
                                TrackUI(it)
                            })
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

                })
        }
    }

    fun removeSearchedText() {
        renderState(SearchState.Content(tracks = mutableListOf()))
    }

    fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun getHistoryState(): HistoryState {
        val historyTracks = historyInteractor.getSearchHistory()
        return if (historyTracks.isEmpty()) {
            HistoryState.Empty
        } else {
            HistoryState.Content(historyTracks.map {
                TrackUI(it)
            })
        }
    }

    fun addToHistory(track: TrackUI) {
        return historyInteractor.addToSearchHistory(track.convertToTrack())
    }

    fun deleteHistory() {
        return historyInteractor.deleteSearchHistory()
    }

    fun saveHistory() {
        return historyInteractor.saveSearchHistory()
    }
}
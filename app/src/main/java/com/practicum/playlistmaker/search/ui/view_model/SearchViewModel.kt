package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.util.Creator

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val historyInteractor = Creator.getHistoryInteractor(getApplication())

    private val searchInteractor = Creator.getSearchInteractor()
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

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchByClick(searchText: String) {
        if (latestSearchText == searchText) {
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
}
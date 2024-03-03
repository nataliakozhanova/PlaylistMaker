package com.practicum.playlistmaker.search.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.view.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val historyAdapter = TracksAdapter(
        object : TracksAdapter.SearchClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    openPlayer(track)
                }
            }
        }
    )

    private val searchAdapter = TracksAdapter(
        object : TracksAdapter.SearchClickListener {
            override fun onTrackClick(track: Track) {
                viewModel.addToHistory(track)
                if (clickDebounce()) {
                    openPlayer(track)
                }
            }
        }
    )

    private lateinit var textWatcher: TextWatcher

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        binding.tracksRecyclerView.adapter = searchAdapter
        binding.searchHistoryRecyclerView.adapter = historyAdapter

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                binding.searchClearButton.visibility =
                    clearButtonVisibility(binding.searchEditText.editableText)
                if (binding.searchEditText.hasFocus() && s.isNullOrEmpty()) {
                    renderHistory(viewModel.getHistoryState())
                } else {
                    showEmptyHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        viewModel.observeState().observe(this) {
            renderSearch(it)
        }

        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                renderHistory(viewModel.getHistoryState())
            } else {
                showEmptyHistory()
            }
        }

        binding.deleteSearchHistoryButton.setOnClickListener {
            viewModel.deleteHistory()
            showEmptyHistory()
        }

        binding.searchClearButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                binding.searchEditText.windowToken,
                0
            )
            binding.searchEditText.setText("")
            viewModel.removeSearchedText()
            binding.searchClearButton.visibility = View.GONE
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchByClick(binding.searchEditText.text.toString())
                true
            }
            false
        }

        binding.updateButton.setOnClickListener {
            viewModel.searchByClick(binding.searchEditText.text.toString())
        }

    }

    override fun onStop() {
        super.onStop()
        viewModel.saveHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
        intent.putExtra(Track.INTENT_KEY, track)
        startActivity(intent)
    }

    private fun clearButtonVisibility(editable: Editable?): Int {
        return if (editable.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun renderSearch(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty(state.emptyMessage)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Loading -> showLoading()
        }
    }

    private fun hideError() {
        binding.searchErrorContainer.visibility = ViewGroup.GONE
        binding.errorInternetImage.visibility = View.GONE
        binding.errorSearchImage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.searchErrorTextView.visibility = View.GONE
        binding.errorUnionBigImage.visibility = View.GONE
        binding.errorUnionSmallImage.visibility = View.GONE
    }

    private fun showLoading() {
        binding.tracksRecyclerView.visibility = View.GONE
        hideError()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(emptyMessage: String) {
        binding.tracksRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        binding.searchErrorContainer.visibility = ViewGroup.VISIBLE
        binding.errorSearchImage.visibility = View.VISIBLE
        binding.errorUnionBigImage.visibility = View.VISIBLE
        binding.errorUnionSmallImage.visibility = View.VISIBLE
        binding.errorInternetImage.visibility = View.GONE
        binding.updateButton.visibility = View.GONE

        binding.searchErrorTextView.visibility = View.VISIBLE
        binding.searchErrorTextView.text = emptyMessage
    }

    private fun showError(errorMessage: String) {
        binding.tracksRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        binding.searchErrorContainer.visibility = ViewGroup.VISIBLE
        binding.errorInternetImage.visibility = View.VISIBLE
        binding.updateButton.visibility = View.VISIBLE
        binding.errorSearchImage.visibility = View.GONE
        binding.errorUnionBigImage.visibility = View.VISIBLE
        binding.errorUnionSmallImage.visibility = View.VISIBLE

        binding.searchErrorTextView.visibility = View.VISIBLE
        binding.searchErrorTextView.text = errorMessage
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        hideError()
        binding.tracksRecyclerView.visibility = View.VISIBLE
        searchAdapter.tracks.clear()
        searchAdapter.tracks.addAll(tracks)
        searchAdapter.notifyDataSetChanged()
    }

    private fun renderHistory(state: HistoryState) {
        searchAdapter.tracks.clear()
        searchAdapter.notifyDataSetChanged()
        when (state) {
            is HistoryState.Content -> showHistory(state.tracks)
            is HistoryState.Empty -> showEmptyHistory()
        }
    }

    private fun showHistory(tracks: List<Track>) {
        hideError()
        binding.searchHistoryContainer.visibility = ViewGroup.VISIBLE
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
    }

    private fun showEmptyHistory() {
        hideError()
        binding.searchHistoryContainer.visibility = ViewGroup.GONE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}

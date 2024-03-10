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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.player.ui.view.PlayerActivity
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.search.ui.models.TrackUI
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewModel by viewModel<SearchViewModel>()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

    private val historyAdapter = TracksAdapter(
        object : TracksAdapter.SearchClickListener {
            override fun onTrackClick(track: TrackUI) {
                if (clickDebounce()) {
                    openPlayer(track)
                }
            }
        }
    )

    private val searchAdapter = TracksAdapter(
        object : TracksAdapter.SearchClickListener {
            override fun onTrackClick(track: TrackUI) {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        textWatcher.let { binding.searchEditText.addTextChangedListener(it) }

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
        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
    }

    private fun openPlayer(track: TrackUI) {

        if (track.previewUrl.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_url), Toast.LENGTH_LONG)
                .show()
        } else {
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(TrackUI.INTENT_KEY, track)
            startActivity(intent)
        }
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
        with(binding) {
            searchErrorContainer.visibility = ViewGroup.GONE
            errorInternetImage.visibility = View.GONE
            errorSearchImage.visibility = View.GONE
            updateButton.visibility = View.GONE
            searchErrorTextView.visibility = View.GONE
            errorUnionBigImage.visibility = View.GONE
            errorUnionSmallImage.visibility = View.GONE
        }
    }

    private fun showLoading() {
        binding.tracksRecyclerView.visibility = View.GONE
        hideError()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(emptyMessage: String) {
        with(binding) {
            tracksRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE

            searchErrorContainer.visibility = ViewGroup.VISIBLE
            errorSearchImage.visibility = View.VISIBLE
            errorUnionBigImage.visibility = View.VISIBLE
            errorUnionSmallImage.visibility = View.VISIBLE
            errorInternetImage.visibility = View.GONE
            updateButton.visibility = View.GONE

            searchErrorTextView.visibility = View.VISIBLE
            searchErrorTextView.text = emptyMessage
        }
    }

    private fun showError(errorMessage: String) {
        with(binding) {
            tracksRecyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE

            searchErrorContainer.visibility = ViewGroup.VISIBLE
            errorInternetImage.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE
            errorSearchImage.visibility = View.GONE
            errorUnionBigImage.visibility = View.VISIBLE
            errorUnionSmallImage.visibility = View.VISIBLE

            searchErrorTextView.visibility = View.VISIBLE
            searchErrorTextView.text = errorMessage
        }
    }

    private fun showContent(tracks: List<TrackUI>) {
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

    private fun showHistory(tracks: List<TrackUI>) {
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
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

}

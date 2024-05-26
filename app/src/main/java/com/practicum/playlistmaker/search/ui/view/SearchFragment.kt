package com.practicum.playlistmaker.search.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.main.ui.RootActivity
import com.practicum.playlistmaker.player.ui.view.PlayerActivity
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.search.ui.models.TrackUI
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

    private lateinit var onTrackClickDebounce: (TrackUI) -> Unit

    private var historyAdapter: TracksAdapter? = null
    private var searchAdapter: TracksAdapter? = null


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<TrackUI>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openPlayer(track)
        }

        historyAdapter = TracksAdapter { track ->
            (activity as RootActivity).hideBottomNavigationView()
            onTrackClickDebounce(track)

        }

        searchAdapter = TracksAdapter { track ->
            viewModel.addToHistory(track)
            (activity as RootActivity).hideBottomNavigationView()
            onTrackClickDebounce(track)

        }

        binding.tracksRecyclerView.adapter = searchAdapter
        binding.searchHistoryRecyclerView.adapter = historyAdapter

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

        viewModel.observeState().observe(viewLifecycleOwner) {
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
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                binding.searchEditText.windowToken,
                0
            )
            binding.searchEditText.setText("")
            viewModel.removeSearchedText()
            binding.searchClearButton.isVisible = false
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

    override fun onResume() {
        super.onResume()
        (activity as RootActivity).showBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        historyAdapter = null
        searchAdapter = null
        binding.tracksRecyclerView.adapter = null
        binding.searchHistoryRecyclerView.adapter = null
        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
        _binding = null
    }

    private fun openPlayer(track: TrackUI) {

        if (track.previewUrl.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.empty_url), Toast.LENGTH_LONG)
                .show()
        } else {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
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
            searchErrorContainer.isVisible = false
            searchErrorImageView.isVisible = false
            updateButton.isVisible = false
            searchErrorTextView.isVisible = false
        }
    }

    private fun showLoading() {
        binding.tracksRecyclerView.isVisible = false
        hideError()
        binding.progressBar.isVisible = true
    }

    private fun showEmpty(emptyMessage: String) {
        with(binding) {
            tracksRecyclerView.isVisible = false
            progressBar.isVisible = false

            searchErrorContainer.isVisible = true
            searchErrorImageView.isVisible = true
            searchErrorImageView.setImageResource(R.drawable.empty_icon)

            updateButton.isVisible = false

            searchErrorTextView.isVisible = true
            searchErrorTextView.text = emptyMessage
        }
    }

    private fun showError(errorMessage: String) {
        with(binding) {
            tracksRecyclerView.isVisible = false
            progressBar.isVisible = false

            searchErrorContainer.isVisible = true
            searchErrorImageView.isVisible = true
            updateButton.isVisible = true
            searchErrorImageView.setImageResource(R.drawable.internet_error_icon)
            searchErrorTextView.isVisible = true
            searchErrorTextView.text = errorMessage
        }
    }

    private fun showContent(tracks: List<TrackUI>) {
        binding.progressBar.isVisible = false
        hideError()
        binding.tracksRecyclerView.isVisible = true
        if (searchAdapter == null) {
            return
        }
        searchAdapter!!.tracks.clear()
        searchAdapter!!.tracks.addAll(tracks)
        searchAdapter!!.notifyDataSetChanged()
    }

    private fun renderHistory(state: HistoryState) {
        if (searchAdapter == null) {
            return
        }
        searchAdapter!!.tracks.clear()
        searchAdapter!!.notifyDataSetChanged()
        when (state) {
            is HistoryState.Content -> showHistory(state.tracks)
            is HistoryState.Empty -> showEmptyHistory()
        }
    }

    private fun showHistory(tracks: List<TrackUI>) {
        hideError()
        binding.searchHistoryContainer.isVisible = true
        if (historyAdapter == null) {
            return
        }
        historyAdapter!!.tracks.clear()
        historyAdapter!!.tracks.addAll(tracks)
        historyAdapter!!.notifyDataSetChanged()
    }

    private fun showEmptyHistory() {
        hideError()
        binding.searchHistoryContainer.isVisible = false
    }

}
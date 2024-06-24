package com.practicum.playlistmaker.search.ui.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.history.ui.models.HistoryState
import com.practicum.playlistmaker.player.ui.view.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val historyAdapter = TracksAdapter(
        { track ->
            onTrackClickDebounce(track)
        },
        { _ -> return@TracksAdapter })

    private val searchAdapter = TracksAdapter(
        { track ->
            viewModel.addToHistory(track)
            onTrackClickDebounce(track)
        },
        { _ -> return@TracksAdapter })

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tracksRecyclerView.adapter = searchAdapter
        binding.searchHistoryRecyclerView.adapter = historyAdapter

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openPlayer(track)
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

    override fun onStop() {
        super.onStop()
        viewModel.saveHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
        _binding = null
    }

    private fun openPlayer(track: Track) {
        if (track.previewUrl.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.empty_url), Toast.LENGTH_LONG)
                .show()
        } else {
            viewModel.checkFavorites(track)
            val bundle = bundleOf(PlayerFragment.TRACK_KEY to track)
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.playerFragment, bundle)
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

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.isVisible = false
        hideError()
        binding.tracksRecyclerView.isVisible = true
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
        binding.searchHistoryContainer.isVisible = true
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
    }

    private fun showEmptyHistory() {
        hideError()
        binding.searchHistoryContainer.isVisible = false
    }

}
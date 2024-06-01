package com.practicum.playlistmaker.library.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentLibraryFavoritesBinding
import com.practicum.playlistmaker.favorites.presentation.models.FavoritesState
import com.practicum.playlistmaker.favorites.presentation.view_model.LibraryFavoritesViewModel
import com.practicum.playlistmaker.player.ui.view.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view.TracksAdapter
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFavoritesFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        fun newInstance() = LibraryFavoritesFragment()
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val favoritesAdapter = TracksAdapter { track ->
        onTrackClickDebounce(track)
    }

    private var _binding: FragmentLibraryFavoritesBinding? = null
    private val binding get() = _binding!!

    private val libraryFavoritesViewModel: LibraryFavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryFavoritesViewModel.getFavorites()

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openPlayer(track)
        }

        binding.favoritesRecyclerView.adapter = favoritesAdapter

        libraryFavoritesViewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        libraryFavoritesViewModel.getFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderState(state: FavoritesState) {
        favoritesAdapter.tracks.clear()
        favoritesAdapter.notifyDataSetChanged()
        when (state) {
            is FavoritesState.Empty -> showEmpty()
            is FavoritesState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty(): Unit = with(binding) {
        favoritesRecyclerView.isVisible = false
        emptyImageView.isVisible = true
        favoriteEmptyTextView.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.emptyImageView.isVisible = false
        binding.favoriteEmptyTextView.isVisible = false
        binding.favoritesRecyclerView.isVisible = true

        favoritesAdapter.tracks.clear()
        favoritesAdapter.tracks.addAll(tracks)
        favoritesAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {

        if (track.previewUrl.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.empty_url), Toast.LENGTH_LONG)
                .show()
        } else {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(Track.INTENT_KEY, track)
            startActivity(intent)
        }
    }

}
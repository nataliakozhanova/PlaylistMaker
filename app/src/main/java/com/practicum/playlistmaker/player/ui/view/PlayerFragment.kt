package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.extensions.safeGetParcelableExtra
import com.practicum.playlistmaker.player.ui.models.FavoriteState
import com.practicum.playlistmaker.player.ui.models.PlayerVMState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.playlist.presentation.models.PlaylistsState
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    companion object {
        const val TRACK_KEY = "track"
    }

    private var track = Track()

    private val playlistsBottomSheetAdapter = PlaylistsBottomSheetAdapter { playlist ->
        val isTrackAdded = viewModel.addTrackToPlaylist(track, playlist)
        if (isTrackAdded) {
            Toast
                .makeText(
                    requireContext(),
                    getString(R.string.toast_track_exists_in_playlist, playlist.playlistName),
                    Toast.LENGTH_SHORT
                )
                .show()
        } else {
            Toast
                .makeText(
                    requireContext(),
                    getString(R.string.toast_track_added_to_playlist, playlist.playlistName),
                    Toast.LENGTH_SHORT
                )
                .show()
            renderBottomSheet()
        }
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = requireArguments().safeGetParcelableExtra(TRACK_KEY)

        binding.playlistsBottomSheetRecyclerView.adapter = playlistsBottomSheetAdapter

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.playerToolbar)
        binding.playerToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderPlayer(it)
        }

        viewModel.observeFavoriteState().observe(viewLifecycleOwner) {
            renderFavorite(it)
        }


        viewModel.preparePlayer(track.previewUrl)

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.playerFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        val trackCoverUrl = track.getArtworkUrl512()
        val trackCornerRadius: Int =
            requireActivity().applicationContext.resources.getDimensionPixelSize(R.dimen.dp8)

        Glide.with(requireActivity().applicationContext)
            .load(trackCoverUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(trackCornerRadius))
            .into(binding.trackCoverImageView)

        with(binding) {
            playerTrackNameTextView.text = track.trackName
            valueTrackTimeTextView.text = track.trackTime
            playerArtistNameTextView.text = track.artistName
            valueCollectionTextView.text = track.collectionName
            valueReleaseYearTextView.text = track.releaseDate
            valueGenreTextView.text = track.primaryGenreName
            valueCountryTextView.text = track.country
        }

        if (track.isFavorite) {
            binding.playerFavoriteButton.setImageResource(R.drawable.favorite_button_on)
        } else {
            binding.playerFavoriteButton.setImageResource(R.drawable.favorite_button)
        }
        binding.overlay.isVisible = false
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.addToPlaylistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.observePlaylistState().observe(/* owner = */ viewLifecycleOwner) {
            renderPlaylistState(it)
        }

        binding.playerAddToPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            renderBottomSheet()
        }

        binding.newPlaylistBottomSheetButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(p0: View, p1: Float) {}

        })

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayback()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumePlayback()
    }

    private fun renderPlayer(state: PlayerVMState) {
        when (state) {
            is PlayerVMState.Prepared -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.play_button_100_100)
                binding.timelineTextView.text = getString(R.string.timeline_mock)
            }

            is PlayerVMState.Playing -> {
                binding.playButton.setImageResource(R.drawable.pause_button_100_100)
                binding.timelineTextView.text = state.elapsedTime
            }

            is PlayerVMState.Pause -> {
                binding.playButton.setImageResource(R.drawable.play_button_100_100)
                binding.timelineTextView.text = state.elapsedTime
            }

            else -> {}
        }
    }

    private fun renderFavorite(state: FavoriteState) {
        when (state) {
            is FavoriteState.IsFavorite -> {
                if (state.isFavorite) {
                    binding.playerFavoriteButton.setImageResource(R.drawable.favorite_button_on)
                } else {
                    binding.playerFavoriteButton.setImageResource(R.drawable.favorite_button)
                }
            }

            else -> {}
        }
    }

    private fun renderBottomSheet() {
        viewModel.getPlaylists()
    }

    private fun renderPlaylistState(state: PlaylistsState) {
        playlistsBottomSheetAdapter.playlists.clear()
        playlistsBottomSheetAdapter.notifyDataSetChanged()

        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            else -> {}
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistsBottomSheetRecyclerView.isVisible = true

        playlistsBottomSheetAdapter.playlists.clear()
        playlistsBottomSheetAdapter.playlists.addAll(playlists)
        playlistsBottomSheetAdapter.notifyDataSetChanged()
    }
}

package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.player.ui.view.PlayerFragment
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.playlist.presentation.models.SelectedPlaylistsState
import com.practicum.playlistmaker.playlist.presentation.view_model.PlaylistViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view.TracksAdapter
import com.practicum.playlistmaker.util.debounce
import com.practicum.playlistmaker.util.getCountableMitutes
import com.practicum.playlistmaker.util.getCountableTracks
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {
    companion object {
        private const val ARGS_PLAYLIST_ID = "playlistID"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

        fun createArgs(playlistID: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistID)
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val playlistViewModel: PlaylistViewModel by viewModel {
        parametersOf(requireArguments().getInt(ARGS_PLAYLIST_ID))
    }

    private val trackPlaylistsAdapter = TracksAdapter({ track ->
        onTrackClickDebounce(track)
    }, { track -> showTrackDeletingDialog(track) })

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            openPlayer(track)
        }

        binding.tracksInPlaylistBottomSheetRecyclerView.adapter = trackPlaylistsAdapter

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.playlistToolbar)
        binding.playlistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.sharePlaylistButton.setOnClickListener {
            sharePlaylist()
        }

        binding.overlay.isVisible = false
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.menuPlaylistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.morePlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            renderMenuBottomSheet()
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

        binding.deletePlaylistTextView.setOnClickListener {
            showPlaylistDeletingDialog()
        }

        binding.editPlaylistTextView.setOnClickListener {
            editPlaylistInfo(playlistViewModel.playlist)
        }

        binding.sharePlaylistTextView.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

    }

    override fun onResume() {
        super.onResume()
        playlistViewModel.renderFragment()
    }

    private fun renderState(state: SelectedPlaylistsState) {
        when (state) {
            is SelectedPlaylistsState.Content -> showContent(
                state.playlist,
                state.duration,
                state.tracks
            )

            else -> {}
        }
    }

    private fun showContent(playlist: Playlist, duration: Int, tracks: List<Track>) {
        Glide.with(requireActivity().applicationContext)
            .load(playlist.coverUri)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop())
            .into(binding.playlistOpenedCoverImageView)

        with(binding) {
            playlistOpenedNameTextView.text = playlist.playlistName
            if (playlist.playlistDescription.isEmpty()) {
                playlistOpenedDescriptionTextView.isVisible = false
            } else {
                playlistOpenedDescriptionTextView.text = playlist.playlistDescription
            }
            playlistOpenedDurationTextView.text =
                getCountableMitutes(duration, requireContext().resources)
            playlistOpenedTracksNumberTextView.text =
                getCountableTracks(playlist.numberOfTracks, requireContext().resources)
            tracksInPlaylistBottomSheetRecyclerView.isVisible = true
        }

        trackPlaylistsAdapter.tracks.clear()
        trackPlaylistsAdapter.tracks.addAll(tracks)
        trackPlaylistsAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        if (track.previewUrl.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.empty_url), Toast.LENGTH_LONG)
                .show()
        } else {
            val bundle = bundleOf(PlayerFragment.TRACK_KEY to track)
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.playerFragment, bundle)
        }
    }

    private fun onTrackDelete(track: Track) {
        playlistViewModel.deleteTrackFromPlaylist(track.trackId)
        val position = trackPlaylistsAdapter.tracks.indexOf(track)
        trackPlaylistsAdapter.tracks.remove(track)
        trackPlaylistsAdapter.notifyItemRemoved(position)
        trackPlaylistsAdapter.notifyItemRangeChanged(position, trackPlaylistsAdapter.tracks.size)
    }

    private fun showTrackDeletingDialog(track: Track) {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.deleting_dialog_title)
            .setNegativeButton(R.string.deleting_dialog_negative) { dialog, which ->
                dialog.dismiss()
                binding.overlay.isVisible = false
            }.setPositiveButton(R.string.deleting_dialog_positive) { dialog, which ->
                dialog.dismiss()
                onTrackDelete(track)
                binding.overlay.isVisible = false
            }
            .create()
        dialog.show()
        binding.overlay.isVisible = true
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

    }

    private fun renderMenuBottomSheet() {
        val currentPlaylist = playlistViewModel.getCurrentPlaylist()

        val playlistCornerRadius: Int =
            requireActivity().applicationContext.resources.getDimensionPixelSize(R.dimen.track_corner_radius)

        Glide.with(requireActivity().applicationContext)
            .load(currentPlaylist.coverUri)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(playlistCornerRadius))
            .into(binding.menuPlaylistCoverImageView)


        with(binding) {
            menuPlaylistNameTextView.text = currentPlaylist.playlistName
            menuNumberOfTracksTextView.text = getCountableTracks(
                currentPlaylist.numberOfTracks,
                requireActivity().applicationContext.resources
            )
        }
    }

    private fun showPlaylistDeletingDialog() {
        val currentPlaylist = playlistViewModel.getCurrentPlaylist()
        val title =
            getString(R.string.dialog_title_playlist_menu_delete, currentPlaylist.playlistName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setNegativeButton(R.string.deleting_dialog_negative) { dialog, which ->
                dialog.dismiss()
            }.setPositiveButton(R.string.deleting_dialog_positive) { dialog, which ->
                dialog.dismiss()
                playlistViewModel.deletePlaylist()
                findNavController().navigateUp()
            }
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun editPlaylistInfo(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_editingPlaylistFragment,
            EditingPlaylistFragment.createArgs(playlist.playlistID)
        )
    }

    private fun sharePlaylist() {
        if (!playlistViewModel.isHasTracks()) {
            Toast.makeText(requireContext(), R.string.toast_no_tracks_to_share, Toast.LENGTH_SHORT)
                .show()
        } else {
            startActivity(playlistViewModel.getSharingPlaylistIntent(requireActivity().applicationContext.resources))
        }
    }

}
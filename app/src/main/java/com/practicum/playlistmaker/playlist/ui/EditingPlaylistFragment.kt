package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.presentation.models.PlaylistInfoState
import com.practicum.playlistmaker.playlist.presentation.view_model.EditingPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.InputStream

class EditingPlaylistFragment() : NewPlaylistFragment() {

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlistID"

        fun createArgs(playlistID: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (managedPlaylistViewModel as EditingPlaylistViewModel).observeState()
            .observe(viewLifecycleOwner) {
                renderState(it as PlaylistInfoState.Content)
            }

        binding.createPlaylistButton.isEnabled = true

        binding.newPlaylistToolbar.title =
            requireActivity().getText(R.string.editing_playlist_toolbar)
        binding.createPlaylistButton.text =
            requireActivity().getText(R.string.editing_playlist_save_button)

    }

    override fun onCreateViewModel() {
        managedPlaylistViewModel = viewModel<EditingPlaylistViewModel> {
            parametersOf(requireArguments().getInt(ARGS_PLAYLIST_ID))
        }.value
    }

    private fun updatePlaylist() {
        val name = binding.playlistNameInputEditText.text.toString()
        val description = binding.playlistDescriptionInputEditText.text?.toString() ?: ""
        var inputStream: InputStream? = null
        val externalDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        coverUri?.let {
            inputStream = requireActivity().contentResolver.openInputStream(coverUri!!)
        }
        managedPlaylistViewModel.savePlaylistInfo(name, description, inputStream, externalDir!!)
    }


    override fun onBackPressedNavigation() {
        findNavController().navigateUp()
    }

    override fun onSavePlaylistClick() {
        updatePlaylist()
        findNavController().navigateUp()
    }

    private fun renderState(state: PlaylistInfoState.Content) {
        if (state.coverUri.isEmpty()) {
            binding.addPhotoImageView.isVisible = true
        } else {
            binding.addPhotoImageView.isVisible = false
            renderCover(state.coverUri)
        }
        with(binding) {
            playListCoverImageView.isVisible = true
            playlistNameInputEditText.setText(state.playlistName)
            playlistDescriptionInputEditText.setText(state.playlistDescription)
        }
    }

}
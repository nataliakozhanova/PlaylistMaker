package com.practicum.playlistmaker.library.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentLibraryPlaylistsBinding
import com.practicum.playlistmaker.library.ui.models.PlaylistsState
import com.practicum.playlistmaker.library.ui.view_model.LibraryPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryPlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryPlaylistsFragment()
    }

    private val libraryPlaylistsViewModel: LibraryPlaylistsViewModel by viewModel()

    private lateinit var binding: FragmentLibraryPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryPlaylistsViewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
        }
    }
    private fun showEmpty() {
        binding.newPlaylistButton.visibility = View.VISIBLE
        binding.emptyPlaylistsIconContainer.visibility = ViewGroup.VISIBLE
        binding.playlistsEmptyTextView.visibility = View.VISIBLE
    }

}
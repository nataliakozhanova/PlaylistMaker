package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentLibraryPlaylistsBinding
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.playlist.presentation.models.PlaylistsState
import com.practicum.playlistmaker.playlist.presentation.view_model.LibraryPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryPlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryPlaylistsFragment()
    }

    private val libraryPlaylistsViewModel: LibraryPlaylistsViewModel by viewModel()

    private var _binding: FragmentLibraryPlaylistsBinding? = null
    private val binding get() = _binding!!

    private var playlistsAdapter : PlaylistAdapter = PlaylistAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryPlaylistsViewModel.getPlaylists()

        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        libraryPlaylistsViewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        libraryPlaylistsViewModel.getPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderState(state: PlaylistsState) {
        playlistsAdapter.playlists.clear()
        playlistsAdapter.notifyDataSetChanged()
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }
    private fun showEmpty(): Unit = with(binding) {
        newPlaylistButton.isVisible = true
        emptyImageView.isVisible = true
        playlistsEmptyTextView.isVisible = true
        playlistsRecyclerView.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.emptyImageView.isVisible = false
        binding.playlistsEmptyTextView.isVisible = false
        binding.playlistsRecyclerView.isVisible = true

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

}


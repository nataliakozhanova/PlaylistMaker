package com.practicum.playlistmaker.library.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentLibraryFavoritesBinding
import com.practicum.playlistmaker.library.ui.models.FavoritesState
import com.practicum.playlistmaker.library.ui.view_model.LibraryFavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFavoritesFragment()
    }

    private val libraryFavoritesViewModel: LibraryFavoritesViewModel by viewModel()

    private lateinit var binding: FragmentLibraryFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryFavoritesViewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showEmpty()
        }
    }
    private fun showEmpty() {
        binding.emptyFavoritesIconContainer.visibility = ViewGroup.VISIBLE
        binding.favoriteEmptyTextView.visibility = View.VISIBLE
    }

}
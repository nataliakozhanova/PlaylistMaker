package com.practicum.playlistmaker.library.ui.view

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.favorites.ui.LibraryFavoritesFragment
import com.practicum.playlistmaker.playlist.ui.LibraryPlaylistsFragment


class LibraryViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        private const val FRAGMENTS_COUNT = 2
    }

    override fun getItemCount(): Int {
        return FRAGMENTS_COUNT
    }

    override fun createFragment(position: Int) = when (position) {
        0 -> LibraryFavoritesFragment.newInstance()
        else -> LibraryPlaylistsFragment.newInstance()
    }
}
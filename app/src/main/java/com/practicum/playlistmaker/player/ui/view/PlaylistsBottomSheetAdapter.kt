package com.practicum.playlistmaker.player.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.playlist.domain.models.Playlist


class PlaylistsBottomSheetAdapter(
    private val playlistsClickListener: PlaylistsClickListener
) : RecyclerView.Adapter<PlaylistsBottomSheetViewHolder>() {

    var playlists: ArrayList<Playlist> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsBottomSheetViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsBottomSheetViewHolder(playlistsClickListener, PlaylistViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistsBottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun interface PlaylistsClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

}
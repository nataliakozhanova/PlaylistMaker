package com.practicum.playlistmaker.playlist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistCardviewBinding
import com.practicum.playlistmaker.playlist.domain.models.Playlist

class PlaylistAdapter() : RecyclerView.Adapter<PlaylistsViewHolder>() {

    var playlists: ArrayList<Playlist> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(PlaylistCardviewBinding.inflate(layoutInspector, parent, false) )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

}
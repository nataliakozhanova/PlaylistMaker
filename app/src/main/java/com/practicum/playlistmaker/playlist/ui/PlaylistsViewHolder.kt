package com.practicum.playlistmaker.playlist.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistCardviewBinding
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.util.getCountableTracks

class PlaylistsViewHolder(
    private val clickListener: PlaylistsAdapter.PlaylistClickListener,
    private val binding: PlaylistCardviewBinding,
) :
    RecyclerView.ViewHolder(binding.root) {

    private val playlistCornerRadius: Int =
        itemView.context.resources.getDimensionPixelSize(R.dimen.dp8)

    fun bind(item: Playlist) {
        Glide.with(itemView)
            .load(item.coverUri)
            .placeholder(R.drawable.placeholder)

            .transform(CenterCrop(), RoundedCorners(playlistCornerRadius))
            .into(binding.playlistCoverImageView)

        binding.playlistNameTextView.text = item.playlistName

        binding.numberOfTracksTextView.text =
            getCountableTracks(item.numberOfTracks, itemView.context.resources)

        itemView.setOnClickListener { clickListener.onPlaylistClick(item) }
    }

}
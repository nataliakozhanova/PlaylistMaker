package com.practicum.playlistmaker.player.ui.view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.util.getCountableTracks

class PlaylistsBottomSheetViewHolder(
    private val clickListener: PlaylistsBottomSheetAdapter.PlaylistsClickListener,
    private val binding: PlaylistViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val playlistCornerRadius: Int =
        itemView.context.resources.getDimensionPixelSize(R.dimen.track_corner_radius)

    fun bind(item: Playlist) {
        Glide.with(itemView)
            .load(item.coverUri)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(),RoundedCorners(playlistCornerRadius))
            .into(binding.playlistBottomSheetImageView)

        binding.playlistNameTextView.text = item.playlistName
        binding.numberOfTracksTextView.text = getCountableTracks(item.numberOfTracks, itemView.context.resources)

        itemView.setOnClickListener { clickListener.onPlaylistClick(item) }
    }

}


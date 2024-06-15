package com.practicum.playlistmaker.playlist.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistCardviewBinding
import com.practicum.playlistmaker.playlist.domain.models.Playlist

class PlaylistsViewHolder(private val binding: PlaylistCardviewBinding) :
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
        binding.numberOfTracksTextView.text = displayNumber(item.numberOfTracks)
    }

    private fun displayNumber(number: Int): String {
        val template: String =
            if ((number == 1) || ((number % 10 == 1) && ((number != 11) && (number % 100 != 11)))) {
                itemView.context.resources.getString(R.string.tracks_sg, number.toString())
            } else if ((number in 2..4) || (number > 21 && ((number % 10 == 2 || number % 10 == 3 || number % 10 == 4) && (number % 100 != 12 && number % 100 != 13 && number % 100 != 14)))) {
                itemView.context.resources.getString(R.string.tracks_db, number.toString())
            } else {
                itemView.context.resources.getString(R.string.tracks_pl, number.toString())
            }
        return template
    }
}
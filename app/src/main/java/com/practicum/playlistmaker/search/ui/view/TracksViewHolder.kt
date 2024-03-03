package com.practicum.playlistmaker.search.ui.view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TracksViewHolder(
    private val clickListener: TracksAdapter.SearchClickListener,
    private val binding: TrackViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val trackCornerRadius: Int =
        itemView.context.resources.getDimensionPixelSize(R.dimen.track_corner_radius)

    fun bind(item: Track) {
        binding.trackNameTextView.text = item.trackName
        binding.trackArtistNameTextView.text = item.artistName
        binding.trackTimeTextView.text = item.getTrackTime()

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(trackCornerRadius))
            .into(binding.trackImageView)

        binding.trackArtistNameTextView.requestLayout()

        itemView.setOnClickListener { clickListener.onTrackClick(item) }
    }
}


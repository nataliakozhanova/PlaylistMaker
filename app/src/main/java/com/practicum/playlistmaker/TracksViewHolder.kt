package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(trackView: View) : RecyclerView.ViewHolder(trackView) {
    private val ivTrackImage: ImageView = trackView.findViewById(R.id.track_image_view)
    private val tvTrackName: TextView = trackView.findViewById(R.id.track_name_text_view)
    private val tvArtistName: TextView = trackView.findViewById(R.id.artist_name_text_view)
    private val tvTrackTime: TextView = trackView.findViewById(R.id.track_time_text_view)
    private val trackCornerRadius: Int =
        itemView.context.resources.getDimensionPixelSize(R.dimen.track_corner_radius)

    fun bind(item: Track) {
        tvTrackName.text = item.trackName
        tvArtistName.text = item.artistName

        tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime)

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(trackCornerRadius))
            .into(ivTrackImage)

        tvArtistName.requestLayout()
    }
}


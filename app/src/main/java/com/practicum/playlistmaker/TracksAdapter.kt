package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    var tracks: List<Track>,
    var trackToSearchHistory : Boolean,
    val searchClickListener: SearchClickListener
) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if(trackToSearchHistory) {
                searchClickListener.onTrackClick(tracks[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface SearchClickListener {
        fun onTrackClick(track: Track)
    }

}

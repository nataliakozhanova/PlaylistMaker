package com.practicum.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.domain.models.Track

class TracksAdapter(
    var tracks: List<Track>,
    var layoutInflater: LayoutInflater,
    val searchClickListener: SearchClickListener
) : RecyclerView.Adapter<TracksViewHolder>() {

    private lateinit var binding : TrackViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        binding = TrackViewBinding.inflate(layoutInflater, parent, false)
        return TracksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            searchClickListener.onTrackClick(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface SearchClickListener {
        fun onTrackClick(track: Track)
    }

}

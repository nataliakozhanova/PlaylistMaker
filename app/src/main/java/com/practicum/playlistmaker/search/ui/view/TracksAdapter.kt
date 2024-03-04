package com.practicum.playlistmaker.search.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TracksAdapter(
    private val searchClickListener: SearchClickListener
) : RecyclerView.Adapter<TracksViewHolder>() {

    var tracks: ArrayList<Track> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {

        val layoutInspector = LayoutInflater.from(parent.context)
        return TracksViewHolder(searchClickListener, TrackViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface SearchClickListener {
        fun onTrackClick(track: Track)
    }

}

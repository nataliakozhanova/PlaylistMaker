package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track
import java.io.Serializable
import java.util.Date

class TrackUI(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable {
    constructor(track: Track) : this(
        trackId = track.trackId,
        trackName = track.trackName?: "",
        artistName = track.artistName?: "",
        trackTime = track.getTrackTime(),
        artworkUrl100 = track.artworkUrl100?: "",
        collectionName = track.collectionName?: "",
        releaseDate = track.releaseDate?: Date(Long.MIN_VALUE),
        primaryGenreName = track.primaryGenreName?: "",
        country = track.country?: "",
        previewUrl = track.previewUrl?: ""
    )

    constructor() : this(
        -1,
        "",
        "",
        "",
        "",
        "",
        Date(Long.MIN_VALUE),
        "",
        "",
        ""
    )

    companion object {
        const val INTENT_KEY = "track"
    }

    fun convertToTrack(): Track {
        return Track(
            trackId,
            trackName,
            artistName,
            getTrackTime(),
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }

    fun getArtworkUrl512(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun getTrackTime(): Long {
        if(trackTime == "") return 0L

        val parts = trackTime.split(":")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        return (minutes * 60 + seconds)*1000
    }
}

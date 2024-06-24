package com.practicum.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Parcelize
class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false,
) : Parcelable {

    constructor(
        trackId: Int,
        trackName: String?,
        artistName: String?,
        trackTime: Long?,
        artworkUrl100: String?,
        collectionName: String?,
        releaseDate: Date?,
        primaryGenreName: String?,
        country: String?,
        previewUrl: String?,
        isFavorite: Boolean = false,
    ) : this(
        trackId = trackId,
        trackName = trackName ?: "",
        artistName = artistName ?: "",
        trackTime = getTrackTime(trackTime),
        artworkUrl100 = artworkUrl100 ?: "",
        collectionName = collectionName ?: "",
        releaseDate = getYear(releaseDate ?: Date(Long.MIN_VALUE)),
        primaryGenreName = primaryGenreName ?: "",
        country = country ?: "",
        previewUrl = previewUrl ?: "",
        isFavorite = isFavorite
    )

    constructor() : this(
        -1,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        false
    )

    fun getArtworkUrl512(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

}

fun getTrackTime(trackTime: Long?): String {
    if (trackTime == null) return ""
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(trackTime)
}

fun getTrackTimeInSeconds(trackTime: String): Int {

    val parts = trackTime.split(":")
    val minutes = parts[0].toIntOrNull() ?: 0
    val seconds = parts[1].toIntOrNull() ?: 0
    return minutes * 60 + seconds
}

private fun getYear(date: Date): String {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal[Calendar.YEAR].toString()
}
package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable {
    companion object {
        const val INTENT_KEY = "track"
    }

    fun getTrackTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }

fun getArtworkUrl512() : String{
    return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
}
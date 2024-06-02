package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

class TrackDto(
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: Date?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Serializable

package com.practicum.playlistmaker.playlist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "added_tracks_table")
data class AddedTrackEntity(
    @PrimaryKey
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
    val isFavorite: Boolean
)


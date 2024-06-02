package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table", indices = [Index(value = ["trackId"], unique = true)])
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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

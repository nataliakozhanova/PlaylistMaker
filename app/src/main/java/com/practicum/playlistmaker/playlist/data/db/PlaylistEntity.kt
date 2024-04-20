package com.practicum.playlistmaker.playlist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playlistName: String,
    val playlistDescription: String,
    val coverUri: String,
    val trackIDList: String,
    val numberOfTracks: Int
)

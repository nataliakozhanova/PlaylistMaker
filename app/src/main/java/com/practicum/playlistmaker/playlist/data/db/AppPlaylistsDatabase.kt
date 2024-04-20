package com.practicum.playlistmaker.playlist.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [PlaylistEntity::class, AddedTrackEntity::class])
abstract class AppPlaylistsDatabase : RoomDatabase(){

        abstract fun playlistsDao(): PlaylistDao
    }

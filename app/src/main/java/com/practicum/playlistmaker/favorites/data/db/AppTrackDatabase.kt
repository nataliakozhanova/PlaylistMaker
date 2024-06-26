package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppTrackDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
}
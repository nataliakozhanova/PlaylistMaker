package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(track: TrackEntity)

    @Query("DELETE FROM favorite_tracks_table WHERE trackId = :trackId")
    suspend fun deleteFavoriteTrack(trackId: Int)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY id DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_tracks_table")
    suspend fun getFavoriteTrackIDs(): List<Int>
}
package com.practicum.playlistmaker.playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY id DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("UPDATE playlists_table SET trackIDList = :trackIDList, numberOfTracks = :numberOfTracks WHERE id = :playlistID")
    suspend fun updatePlaylist(trackIDList: String, numberOfTracks: Int, playlistID: Int)

    @Insert(entity = AddedTrackEntity::class, OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(addedTrack: AddedTrackEntity)
}
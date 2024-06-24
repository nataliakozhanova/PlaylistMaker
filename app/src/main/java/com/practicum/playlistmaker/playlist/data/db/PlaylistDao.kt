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

    @Query("SELECT * FROM playlists_table WHERE id = :playlistID")
    suspend fun getSelectedPlaylist(playlistID: Int): PlaylistEntity

    @Query("UPDATE playlists_table SET trackIDList = :trackIDList, numberOfTracks = :numberOfTracks WHERE id = :playlistID")
    suspend fun updatePlaylist(trackIDList: String, numberOfTracks: Int, playlistID: Int)

    @Query("UPDATE playlists_table SET playlistName = :playlistName, playlistDescription = :playlistDescription, coverUri =:coverUri WHERE id = :playlistID")
    suspend fun updatePlaylistInfo(playlistName: String, playlistDescription: String, coverUri: String, playlistID: Int)

    @Query("DELETE FROM playlists_table WHERE id = :playlistID")
    suspend fun deletePlaylistByID(playlistID: Int)

    @Insert(entity = AddedTrackEntity::class, OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylist(addedTrack: AddedTrackEntity)

    @Query("SELECT * FROM added_tracks_table WHERE trackID in (:trackIDList) ORDER BY trackID DESC")
    suspend fun getTracksByIDs(trackIDList: List<String>): List<AddedTrackEntity>

    @Query("DELETE FROM added_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackByID(trackId: Int)

    @Query("UPDATE added_tracks_table SET isFavorite = :isFavorite WHERE trackId = :trackId")
    suspend fun updateFavoriteByTrackID(trackId: Int, isFavorite: Boolean)
}
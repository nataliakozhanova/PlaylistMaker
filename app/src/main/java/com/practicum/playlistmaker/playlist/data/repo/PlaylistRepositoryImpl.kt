package com.practicum.playlistmaker.playlist.data.repo

import com.practicum.playlistmaker.playlist.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlist.data.db.AddedTrackEntity
import com.practicum.playlistmaker.playlist.data.db.AppPlaylistsDatabase
import com.practicum.playlistmaker.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.playlist.data.storage.FilesStorageApi
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.InputStream

class PlaylistRepositoryImpl(
    private val appPlaylistsDatabase: AppPlaylistsDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val filesStorageApi: FilesStorageApi
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
       val playlistEntity = convertFromPlaylist(playlist)
        appPlaylistsDatabase.playlistsDao().insertPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylist(playlist)
        appPlaylistsDatabase.playlistsDao().updatePlaylist(playlistEntity.trackIDList, playlistEntity.numberOfTracks, playlistEntity.id)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow{
        val allPlaylists = appPlaylistsDatabase.playlistsDao().getPlaylists()
        emit(convertFromPlaylistEntity(allPlaylists))
    }

    override fun saveCoverFile(inputStream: InputStream, externalDir: File): String {
        return filesStorageApi.saveFile(inputStream, externalDir)
    }

    override suspend fun addTrackToPlaylist(track: Track) {
        val addedTrackEntity = convertFromTrack(track)
        appPlaylistsDatabase.playlistsDao().insertTrackToPlaylist(addedTrackEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }
    private fun convertFromPlaylist(playlist: Playlist) : PlaylistEntity {
        return playlistDbConverter.map(playlist)
    }

    private fun convertFromTrack(track: Track): AddedTrackEntity {
        return playlistDbConverter.map(track)
    }
}
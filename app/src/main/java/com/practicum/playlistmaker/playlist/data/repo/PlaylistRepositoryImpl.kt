package com.practicum.playlistmaker.playlist.data.repo

import com.practicum.playlistmaker.playlist.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.playlist.data.db.AddedTrackEntity
import com.practicum.playlistmaker.playlist.data.db.AppPlaylistsDatabase
import com.practicum.playlistmaker.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.playlist.data.storage.FilesStorageApi
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class PlaylistRepositoryImpl(
    private val appPlaylistsDatabase: AppPlaylistsDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val filesStorageApi: FilesStorageApi,
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylist(playlist)
        appPlaylistsDatabase.playlistsDao().insertPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = convertFromPlaylist(playlist)
        appPlaylistsDatabase.playlistsDao().updatePlaylist(
            playlistEntity.trackIDList,
            playlistEntity.numberOfTracks,
            playlistEntity.id
        )
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val allPlaylists = appPlaylistsDatabase.playlistsDao().getPlaylists()
        emit(convertListFromPlaylistEntity(allPlaylists))
    }

    override fun saveCoverFile(inputStream: InputStream, externalDir: File): String {
        return filesStorageApi.saveFile(inputStream, externalDir)
    }

    override suspend fun addTrackToPlaylist(track: Track) {
        val addedTrackEntity = convertFromTrack(track)
        appPlaylistsDatabase.playlistsDao().insertTrackToPlaylist(addedTrackEntity)
    }

    override suspend fun getSelectedPlaylist(playlistID: Int): Playlist {
        val playlistEntity = appPlaylistsDatabase.playlistsDao().getSelectedPlaylist(playlistID)
        return convertFromPlaylistEntity(playlistEntity)
    }

    override fun getTracksByIDs(trackIDList: List<String>): Flow<List<Track>> = flow {
        val tracksInPlaylist = appPlaylistsDatabase.playlistsDao().getTracksByIDs(trackIDList)
        emit(convertListFromAddedTrackEntity(tracksInPlaylist))
    }

    override suspend fun deleteTrackByID(trackID: Int) {
        appPlaylistsDatabase.playlistsDao().deleteTrackByID(trackID)
    }

    override suspend fun deletePlaylistByID(playlist: Playlist) {
        val job = coroutineScope {
            launch(Dispatchers.IO) {
                val playlists = appPlaylistsDatabase.playlistsDao().getPlaylists()
                deleteTrackListFromAddedTracks(playlist.trackIDList, playlists, playlist)
                appPlaylistsDatabase.playlistsDao().deletePlaylistByID(playlist.playlistID)
            }
        }
        job.join()
    }

    override suspend fun updatePlaylistInfo(
        playlistName: String,
        playlistDescription: String,
        coverUri: String,
        playlistID: Int,
    ) {
        appPlaylistsDatabase.playlistsDao()
            .updatePlaylistInfo(playlistName, playlistDescription, coverUri, playlistID)
    }

    private suspend fun deleteTrackListFromAddedTracks(
        trackIDs: List<String>,
        playlists: List<PlaylistEntity>,
        playlistToDelete: Playlist,
    ) {
        for (trackID in trackIDs) {
            var isFound = false
            for (playlist in playlists) {
                if (playlist.id == playlistToDelete.playlistID) {
                    continue
                }
                if (playlist.trackIDList.contains(trackID)) {
                    isFound = true
                    break
                }
            }
            if (!isFound) {
                appPlaylistsDatabase.playlistsDao().deleteTrackByID(trackID.toInt())
            }
        }
    }

    private fun convertListFromAddedTrackEntity(tracks: List<AddedTrackEntity>): List<Track> {
        return tracks.map { addedTrackEntity -> playlistDbConverter.map(addedTrackEntity) }
    }

    private fun convertFromPlaylistEntity(playlistEntity: PlaylistEntity): Playlist {
        return playlistDbConverter.map(playlistEntity)
    }

    private fun convertListFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }

    private fun convertFromPlaylist(playlist: Playlist): PlaylistEntity {
        return playlistDbConverter.map(playlist)
    }

    private fun convertFromTrack(track: Track): AddedTrackEntity {
        return playlistDbConverter.map(track)
    }
}
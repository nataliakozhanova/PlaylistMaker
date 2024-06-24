package com.practicum.playlistmaker.playlist.domain.impl

import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.api.PlaylistRepository
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        for (item in playlist.trackIDList) {
            if (item == track.trackId.toString()) {
                return true
            }
        }
        playlist.numberOfTracks += 1
        playlist.trackIDList.add(track.trackId.toString())
        playlistRepository.updatePlaylist(playlist)
        playlistRepository.addTrackToPlaylist(track)
        return false
    }

    override suspend fun addNewPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override fun saveCoverFile(inputStream: InputStream, externalDir: File): String {
        return playlistRepository.saveCoverFile(inputStream, externalDir)
    }

    override suspend fun getSelectedPlaylist(playlistID: Int): Playlist {
        return playlistRepository.getSelectedPlaylist(playlistID)
    }

    override fun getTracksByIDs(trackIDList: List<String>): Flow<List<Track>> {
        return playlistRepository.getTracksByIDs(trackIDList)
    }

    override suspend fun deleteTrackFromPlaylist(trackID: Int, playlist: Playlist) {
        val job = coroutineScope {
            launch {
                playlist.numberOfTracks -= 1
                playlist.trackIDList.remove(trackID.toString())
                playlistRepository.updatePlaylist(playlist)
                playlistRepository.getAllPlaylists()
                    .collect { playlists -> deleteTrackFromAddedTracks(trackID, playlists) }
            }
        }
        job.join()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylistByID(playlist)
    }

    override suspend fun updatePlaylistInfo(
        playlistName: String,
        playlistDescription: String,
        coverUri: String,
        playlistID: Int,
    ) {
        playlistRepository.updatePlaylistInfo(
            playlistName,
            playlistDescription,
            coverUri,
            playlistID
        )
    }

    private suspend fun deleteTrackFromAddedTracks(trackID: Int, playlists: List<Playlist>) {
        for (list in playlists) {
            for (id in list.trackIDList) {
                if (id == trackID.toString()) return
            }
        }
        playlistRepository.deleteTrackByID(trackID)
    }

}
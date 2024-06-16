package com.practicum.playlistmaker.playlist.data.converters

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.practicum.playlistmaker.playlist.data.db.AddedTrackEntity
import com.practicum.playlistmaker.playlist.data.db.PlaylistEntity
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDbConverter(private val gson: Gson) {
    fun map(playlist: Playlist) : PlaylistEntity {
        val json = gson.toJson(playlist.trackIDList)
        return PlaylistEntity(
            playlist.playlistID,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.coverUri,
            json,
            playlist.numberOfTracks
        )
    }

    fun map(playlistEntity: PlaylistEntity) : Playlist {
        val listType = object : TypeToken<List<String>>() {}.type
        val stringList: List<String> = gson.fromJson(playlistEntity.trackIDList, listType)
        return Playlist(
            playlistEntity.id,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.coverUri,
            stringList.toMutableList(),
            playlistEntity.numberOfTracks
        )
    }
     fun map(addedTrackEntity: AddedTrackEntity): Track {
         return Track(
             addedTrackEntity.trackId,
             addedTrackEntity.trackName,
             addedTrackEntity.artistName,
             addedTrackEntity.trackTime,
             addedTrackEntity.artworkUrl100,
             addedTrackEntity.collectionName,
             addedTrackEntity.releaseDate,
             addedTrackEntity.primaryGenreName,
             addedTrackEntity.country,
             addedTrackEntity.previewUrl,
             addedTrackEntity.isFavorite)
     }

    fun map(track:Track) : AddedTrackEntity {
        return AddedTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }
}
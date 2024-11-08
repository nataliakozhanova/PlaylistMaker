package com.practicum.playlistmaker.playlist.domain.models

class Playlist(
    val playlistID: Int,
    val playlistName: String,
    val playlistDescription: String,
    val coverUri: String,
    val trackIDList: MutableList<String>,
    var numberOfTracks: Int,
) {
    constructor(
        playlistName: String,
        playlistDescription: String,
        coverUri: String,
    ) : this(
        playlistID = 0,
        playlistName = playlistName,
        playlistDescription = playlistDescription,
        coverUri = coverUri,
        trackIDList = mutableListOf(),
        numberOfTracks = 0
    )

    constructor() : this(
        playlistID = 0,
        playlistName = "",
        playlistDescription = "",
        coverUri = "",
        trackIDList = mutableListOf(),
        numberOfTracks = 0
    )
}
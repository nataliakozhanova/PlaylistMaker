package com.practicum.playlistmaker.domain.api

interface PlayerRepository {
    fun setOnPlayerStateChange(playerListener: PlayerListener)
    fun init(previewUrl : String)
    fun play()
    fun pause()
    fun destroy()
    fun getElapsedTime() : Int
}
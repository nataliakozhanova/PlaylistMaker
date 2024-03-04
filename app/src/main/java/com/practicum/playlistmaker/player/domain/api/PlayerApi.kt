package com.practicum.playlistmaker.player.domain.api

interface PlayerApi {
    fun setOnPlayerStateChange(playerListener: PlayerListener)
    fun init(previewUrl : String)
    fun play()
    fun pause()
    fun destroy()
    fun getElapsedTime() : Int
}
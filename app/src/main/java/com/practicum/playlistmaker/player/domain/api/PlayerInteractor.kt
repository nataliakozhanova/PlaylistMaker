package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun init(url : String)
    fun setOnPlayerStateChange(playerListener: PlayerListener)
    fun play()
    fun pause()
    fun destroy()
    fun getElapsedTime() : String
}
package com.practicum.playlistmaker.player.domain.impl

import android.icu.text.SimpleDateFormat
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import java.util.Locale

class PlayerInteractorImpl(private val playerApi: PlayerApi) : PlayerInteractor {
    companion object {
        private const val TIME_OFFSET = 500L
    }
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun init(url : String) {
        playerApi.init(url)
    }

    override fun play() {
        playerApi.play()
    }

    override fun pause() {
        playerApi.pause()
    }

    override fun destroy() {
        playerApi.destroy()
    }

    override fun getElapsedTime() : String {
        return dateFormat.format(playerApi.getElapsedTime()+ TIME_OFFSET)
    }

    override fun setOnPlayerStateChange(playerListener: PlayerListener){
        playerApi.setOnPlayerStateChange(playerListener)
    }
}
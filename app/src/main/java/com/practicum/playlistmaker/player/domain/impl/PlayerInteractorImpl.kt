package com.practicum.playlistmaker.player.domain.impl

import android.icu.text.SimpleDateFormat
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import java.util.Locale

class PlayerInteractorImpl(private val repository: PlayerApi) : PlayerInteractor {
    companion object {
        private const val TIME_OFFSET = 500L
    }
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun init(url : String) {
        repository.init(url)
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun destroy() {
        repository.destroy()
    }

    override fun getElapsedTime() : String {
        return dateFormat.format(repository.getElapsedTime()+ TIME_OFFSET)
    }

    override fun setOnPlayerStateChange(playerListener: PlayerListener){
        repository.setOnPlayerStateChange(playerListener)
    }
}
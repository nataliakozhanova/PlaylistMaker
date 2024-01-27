package com.practicum.playlistmaker.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.PlayerListener
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.PlayerState

class PlayerRepositoryImpl() : PlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private lateinit var playerListener : PlayerListener

    override fun init(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerListener.onPlayerChange(PlayerState.INIT)
        }
        mediaPlayer.setOnCompletionListener {
            playerListener.onPlayerChange(PlayerState.INIT)
        }
    }

    override fun setOnPlayerStateChange(playerListener: PlayerListener){
        this.playerListener = playerListener
    }

    override fun play() {
        mediaPlayer.start()
        playerListener.onPlayerChange(PlayerState.PLAYING)
    }

    override fun pause() {
        mediaPlayer.pause()
        playerListener.onPlayerChange(PlayerState.PAUSED)
    }

    override fun destroy() {
        mediaPlayer.release()
    }

    override fun getElapsedTime(): Int {
        return mediaPlayer.currentPosition
    }

}
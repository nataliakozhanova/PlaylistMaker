package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerApi
import com.practicum.playlistmaker.player.domain.models.PlayerState

class PlayerApiImpl(private val mediaPlayer: MediaPlayer) : PlayerApi {

    private lateinit var playerListener : PlayerListener

    override fun init(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerListener.onPlayerChange(PlayerState.INIT)
        }
        mediaPlayer.setOnCompletionListener {
            playerListener.onPlayerChange(PlayerState.FINISH)
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
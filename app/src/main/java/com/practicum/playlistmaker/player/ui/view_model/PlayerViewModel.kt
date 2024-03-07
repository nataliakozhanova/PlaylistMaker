package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayerVMState

class PlayerViewModel(application: Application, private val playerInteractor: PlayerInteractor) :
    AndroidViewModel(application) {

    companion object {
        private const val DELAY_MILLIS = 300L
    }

    private lateinit var playerStateListener: PlayerListener

    private val handler = Handler(Looper.getMainLooper())

    private var isAutoPaused: Boolean = false

    private val stateLiveData = MutableLiveData<PlayerVMState>(PlayerVMState.Default)
    fun observeState(): LiveData<PlayerVMState> = stateLiveData

    fun preparePlayer(url: String) {
        if (stateLiveData.value != PlayerVMState.Default) {
            stateLiveData.postValue(stateLiveData.value)
            return
        }

        playerStateListener = object : PlayerListener {
            override fun onPlayerChange(state: PlayerState) {
                when (state) {
                    PlayerState.INIT -> {
                        stateLiveData.postValue(PlayerVMState.Prepared)
                    }

                    PlayerState.PLAYING -> {
                        stateLiveData.postValue(PlayerVMState.Playing(playerInteractor.getElapsedTime()))
                        startTimer()
                    }

                    PlayerState.PAUSED -> {
                        stateLiveData.postValue(PlayerVMState.Pause(playerInteractor.getElapsedTime()))
                    }
                }
            }
        }
        playerInteractor.setOnPlayerStateChange(playerStateListener)
        playerInteractor.init(url)
    }


    private fun startPlayer() {
        playerInteractor.play()
    }

    private fun pausePlayer() {
        isAutoPaused = false
        handler?.removeCallbacksAndMessages(null)
        playerInteractor.pause()
    }

    fun playbackControl() {
        when (stateLiveData.value) {
            is PlayerVMState.Playing -> pausePlayer()
            is PlayerVMState.Prepared -> startPlayer()
            is PlayerVMState.Pause -> startPlayer()
            else -> {}
        }

    }

    private fun startTimer() {
        handler?.post(
            createUpdateTimerTask()
        )
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                when (stateLiveData.value) {
                    is PlayerVMState.Playing -> {
                        stateLiveData.postValue(PlayerVMState.Playing(playerInteractor.getElapsedTime()))
                        handler?.postDelayed(this, DELAY_MILLIS)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCleared() {
        playerInteractor.destroy()
    }

    fun pausePlayback() {
        val isPlaying: Boolean = (stateLiveData.value is PlayerVMState.Playing)
        pausePlayer()
        if (isPlaying) isAutoPaused = true

    }

    fun resumePlayback() {
        if (isAutoPaused) {
            startPlayer()
        }
    }

}
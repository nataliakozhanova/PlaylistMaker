package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayerVMState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application, private val playerInteractor: PlayerInteractor) :
    AndroidViewModel(application) {

    companion object {
        private const val DELAY_MILLIS = 300L
    }

    private lateinit var playerStateListener: PlayerListener

    private var timerJob: Job? = null

    private var isAutoPaused: Boolean = false

    private var playerState: PlayerState = PlayerState.INIT

    private val stateLiveData = MutableLiveData<PlayerVMState>(PlayerVMState.Default)
    fun observeState(): LiveData<PlayerVMState> = stateLiveData

    fun preparePlayer(url: String) {
        if (stateLiveData.value != PlayerVMState.Default) {
            stateLiveData.postValue(stateLiveData.value)
            return
        }

        playerStateListener = object : PlayerListener {
            override fun onPlayerChange(state: PlayerState) {
                playerState = state
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

                    PlayerState.FINISH -> {
                        stateLiveData.postValue(PlayerVMState.Prepared)
                        timerJob?.cancel()
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
        timerJob?.cancel()
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
        timerJob = viewModelScope.launch {
            while (playerState == PlayerState.PLAYING) {
                delay(DELAY_MILLIS)
                stateLiveData.postValue(PlayerVMState.Playing(playerInteractor.getElapsedTime()))
            }
        }

    }

    override fun onCleared() {
        playerInteractor.destroy()
    }

    fun pausePlayback() {
        val isPlaying: Boolean = (playerState == PlayerState.PLAYING)
        pausePlayer()
        if (isPlaying) isAutoPaused = true

    }

    fun resumePlayback() {
        if (isAutoPaused) {
            startPlayer()
        }
    }

}
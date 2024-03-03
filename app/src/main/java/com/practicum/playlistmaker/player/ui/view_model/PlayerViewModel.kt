package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.PlayerVMState
import com.practicum.playlistmaker.util.Creator

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val DELAY = 300L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private lateinit var playerStateListener: PlayerListener

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<PlayerVMState>(PlayerVMState.Default)
    fun observeState(): LiveData<PlayerVMState> = stateLiveData

    private val playerInteractor: PlayerInteractor = Creator.getPlayerInteractor()

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
                        handler?.postDelayed(this, DELAY)
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
        pausePlayer()
    }

}
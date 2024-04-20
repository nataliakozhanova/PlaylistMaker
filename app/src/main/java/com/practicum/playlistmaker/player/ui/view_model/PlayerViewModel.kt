package com.practicum.playlistmaker.player.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorites.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerListener
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.player.ui.models.FavoriteState
import com.practicum.playlistmaker.player.ui.models.PlayerVMState
import com.practicum.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.models.Playlist
import com.practicum.playlistmaker.playlist.presentation.models.PlaylistsState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlayerViewModel(
    application: Application,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
) :
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

    private val stateFavoriteLiveData = MutableLiveData<FavoriteState>(FavoriteState.Default)
    fun observeFavoriteState(): LiveData<FavoriteState> = stateFavoriteLiveData

    private val statePlaylistsLiveData = MutableLiveData<PlaylistsState>()
    fun observePlaylistState(): LiveData<PlaylistsState> = statePlaylistsLiveData

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
        timerJob?.cancel()
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

    fun onFavoriteClicked(track: Track) {

        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.deleteTrackFromFavorites(track)
                track.isFavorite = false
            } else {
                track.isFavorite = true
                favoritesInteractor.addTrackToFavorites(track)
            }
            stateFavoriteLiveData.postValue(FavoriteState.IsFavorite(track.isFavorite))
        }

    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderPlaylistsState(PlaylistsState.Empty)
                    } else {
                        renderPlaylistsState(PlaylistsState.Content(playlists))
                    }
                }
        }
    }

    private fun renderPlaylistsState(playlistsState: PlaylistsState) {
        statePlaylistsLiveData.postValue(playlistsState)
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) : Boolean = runBlocking {
        return@runBlocking playlistInteractor.addTrackToPlaylist(track, playlist)
    }
}
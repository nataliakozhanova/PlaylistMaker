package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerListener
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import java.util.Calendar
import java.util.Date

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }

    private lateinit var playerStateListener: PlayerListener

    private var mainThreadHandler: Handler? = null
    lateinit var track: Track
    private lateinit var binding: ActivityPlayerBinding

    private var playerState = STATE_DEFAULT
    private val playerInteractor : PlayerInteractor = Creator.getPlayerInteraktor()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getSerializableExtra(Track.INTENT_KEY) as Track
        mainThreadHandler = Handler(Looper.getMainLooper())

        binding.playerToolbar.setNavigationOnClickListener {
            finish()
        }

        playerStateListener = object : PlayerListener {
            override fun onPlayerChange(state: PlayerState) {
                when (state) {
                    PlayerState.INIT -> {
                        playerState = STATE_PREPARED
                        binding.playButton.isEnabled = true
                        binding.playButton.setImageResource(R.drawable.play_button_100_100)
                        binding.timelineTextView.text = getString(R.string.timeline_mock)
                    }
                    PlayerState.PLAYING -> {
                        playerState = STATE_PLAYING
                        binding.playButton.setImageResource(R.drawable.pause_button_100_100)
                        startTimer()
                    }
                    PlayerState.PAUSED -> {
                        playerState = STATE_PAUSED
                        binding.playButton.setImageResource(R.drawable.play_button_100_100)
                    }
                }
            }
        }
        playerInteractor.setOnPlayerStateChange(playerStateListener)

        preparePlayer()

        binding.playButton.setOnClickListener {
            playbackControl()
        }


        val trackCoverUrl = track.getArtworkUrl512()
        val trackCornerRadius: Int = applicationContext.resources.getDimensionPixelSize(R.dimen.dp8)
        val trackYear = getYear(track.releaseDate)

        Glide.with(applicationContext)
            .load(trackCoverUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(trackCornerRadius))
            .into(binding.trackCoverImageView)

        binding.playerTrackNameTextView.text = track.trackName
        binding.playerArtistNameTextView.text = track.artistName
        binding.valueTrackTimeTextView.text = track.getTrackTime()
        binding.valueCollectionTextView.text = track.collectionName
        binding.valueReleaseYearTextView.text = trackYear
        binding.valueGenreTextView.text = track.primaryGenreName
        binding.valueCountryTextView.text = track.country

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.destroy()
    }

    private fun getYear(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[Calendar.YEAR].toString()
    }


    private fun preparePlayer() {

        playerInteractor.init(track.previewUrl)
    }

    private fun startPlayer() {
        playerInteractor.play()
    }

    private fun pausePlayer() {
        playerInteractor.pause()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        mainThreadHandler?.post(
            createUpdateTimerTask()
        )
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    binding.timelineTextView.text =
                        playerInteractor.getElapsedTime()
                    mainThreadHandler?.postDelayed(this, DELAY)
                }
            }
        }
    }
}


package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.extensions.safeGetParcelableExtra
import com.practicum.playlistmaker.player.ui.models.PlayerVMState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.models.TrackUI
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Date

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.safeGetParcelableExtra(name = TrackUI.INTENT_KEY)

        binding.playerToolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.observeState().observe(this) {
            renderPlayer(it)
        }

        viewModel.preparePlayer(track.previewUrl)

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
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

        with(binding) {
            playerTrackNameTextView.text = track.trackName
            playerArtistNameTextView.text = track.artistName
            valueCollectionTextView.text = track.collectionName
            valueReleaseYearTextView.text = trackYear
            valueGenreTextView.text = track.primaryGenreName
            valueCountryTextView.text = track.country
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayback()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumePlayback()
    }


    private fun getYear(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[Calendar.YEAR].toString()
    }

    private fun renderPlayer(state: PlayerVMState) {
        when (state) {
            is PlayerVMState.Prepared -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.play_button_100_100)
                binding.timelineTextView.text = getString(R.string.timeline_mock)
            }

            is PlayerVMState.Playing -> {
                binding.playButton.setImageResource(R.drawable.pause_button_100_100)
                binding.timelineTextView.text = state.elapsedTime
            }

            is PlayerVMState.Pause -> {
                binding.playButton.setImageResource(R.drawable.play_button_100_100)
                binding.timelineTextView.text = state.elapsedTime
            }

            else -> {}
        }
    }
}


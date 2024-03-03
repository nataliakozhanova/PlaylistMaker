package com.practicum.playlistmaker.player.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.extensions.safeGetSerializableExtra
import com.practicum.playlistmaker.player.ui.models.PlayerVMState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import java.util.Calendar
import java.util.Date

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModels<PlayerViewModel> {
        PlayerViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.safeGetSerializableExtra(Track.INTENT_KEY)

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

        binding.playerTrackNameTextView.text = track.trackName
        binding.playerArtistNameTextView.text = track.artistName
        binding.valueCollectionTextView.text = track.collectionName
        binding.valueReleaseYearTextView.text = trackYear
        binding.valueGenreTextView.text = track.primaryGenreName
        binding.valueCountryTextView.text = track.country

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayback()
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


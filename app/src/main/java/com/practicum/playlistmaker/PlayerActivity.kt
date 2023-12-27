package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val playerArrowBackButton = findViewById<Toolbar>(R.id.player_toolbar)
        playerArrowBackButton.setNavigationOnClickListener {
            finish()
        }
        track = intent.getSerializableExtra(Track.INTENT_KEY) as Track

        val trackCoverIV: ImageView = findViewById(R.id.track_cover_image_view)
        val playerTrackNameTV: TextView = findViewById(R.id.player_track_name_tv)
        val playerArtistNameTV: TextView = findViewById(R.id.player_artist_name_tv)
        val valueTrackTimeTV: TextView = findViewById(R.id.value_track_time_tv)
        val valueCollectionTV: TextView = findViewById(R.id.value_collection_tv)
        val valueReleaseYearTV: TextView = findViewById(R.id.value_release_year_tv)
        val valueGenreTV: TextView = findViewById(R.id.value_genre_tv)
        val valueCountryTV: TextView = findViewById(R.id.value_country_tv)
        val trackCoverUrl = track.getArtworkUrl512()
        val trackCornerRadius: Int = applicationContext.resources.getDimensionPixelSize(R.dimen.dp8)
        val trackYear = getYear(track.releaseDate)

        Glide.with(applicationContext)
            .load(trackCoverUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(trackCornerRadius))
            .into(trackCoverIV)

        playerTrackNameTV.text = track.trackName
        playerArtistNameTV.text = track.artistName
        valueTrackTimeTV.text = track.getTrackTime()
        valueCollectionTV.text = track.collectionName
        valueReleaseYearTV.text = trackYear
        valueGenreTV.text = track.primaryGenreName
        valueCountryTV.text = track.country
    }

    private fun getYear(date: Date?): String {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal[Calendar.YEAR].toString()
    }
}
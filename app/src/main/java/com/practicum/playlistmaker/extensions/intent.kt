package com.practicum.playlistmaker.extensions

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.practicum.playlistmaker.search.domain.models.Track

@Suppress("Deprecation")
fun Intent.safeGetParcelableExtra(name: String): Track {
    val track: Track? = when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getParcelableExtra(name, Track::class.java)
        }

        else -> getParcelableExtra(name)
    }
    if (track != null) return track
    return Track()
}

package com.practicum.playlistmaker.extensions

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.practicum.playlistmaker.search.domain.models.Track

@Suppress("Deprecation")
inline fun Intent.safeGetSerializableExtra(name: String): Track {
    val track : Track
    when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            track = getSerializableExtra(name, Track::class.java) as Track
        }
        else -> track = getSerializableExtra(name) as Track
    }
    if (track != null) return track
    return Track()
}
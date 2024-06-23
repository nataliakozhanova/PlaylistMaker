package com.practicum.playlistmaker.extensions

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import com.practicum.playlistmaker.search.domain.models.Track

@Suppress("Deprecation")
fun Bundle.safeGetParcelableTrack(name: String): Track {
    val track: Track? = when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getParcelable(name, Track::class.java)
        }

        else -> getParcelable(name)
    }
    if (track != null) return track
    return Track()
}

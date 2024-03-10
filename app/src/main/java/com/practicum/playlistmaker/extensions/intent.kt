package com.practicum.playlistmaker.extensions

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.practicum.playlistmaker.search.ui.models.TrackUI

@Suppress("Deprecation")
fun Intent.safeGetParcelableExtra(name: String): TrackUI {
    val track: TrackUI?
    when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            track = getParcelableExtra(name, TrackUI::class.java)
        }

        else -> track = getParcelableExtra(name)
    }
    if (track != null) return track
    return TrackUI()
}

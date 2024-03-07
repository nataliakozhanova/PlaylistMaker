package com.practicum.playlistmaker.extensions

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.practicum.playlistmaker.search.ui.models.TrackUI

@Suppress("Deprecation")
inline fun Intent.safeGetSerializableExtra(name: String): TrackUI {
    val track : TrackUI
    when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            track = getSerializableExtra(name, TrackUI::class.java) as TrackUI
        }
        else -> track = getSerializableExtra(name) as TrackUI
    }
    if (track != null) return track
    return TrackUI()
}
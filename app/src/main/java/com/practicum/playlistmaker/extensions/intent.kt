package com.practicum.playlistmaker.extensions

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import com.practicum.playlistmaker.search.domain.models.Track

@Suppress("Deprecation")
inline fun Intent.safeGetSerializableExtra(name: String): Track = when {
    SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(name, Track::class.java)!!
    else -> getSerializableExtra(name) as Track
}
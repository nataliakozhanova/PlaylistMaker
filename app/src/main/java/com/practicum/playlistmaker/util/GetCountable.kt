package com.practicum.playlistmaker.util

import android.content.res.Resources
import com.practicum.playlistmaker.R

fun getCountable(count: Int, resources: Resources) : String {
    return resources.getQuantityString(R.plurals.plurals_tracks, count, count)
}
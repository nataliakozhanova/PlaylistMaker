package com.practicum.playlistmaker.util

import android.content.res.Resources
import com.practicum.playlistmaker.R

fun getCountableTracks(count: Int, resources: Resources) : String {
    return resources.getQuantityString(R.plurals.plurals_tracks, count, count)
}

fun getCountableMitutes(count: Int, resources: Resources): String {
    return resources.getQuantityString(R.plurals.plurals_mitutes, count, count)
}
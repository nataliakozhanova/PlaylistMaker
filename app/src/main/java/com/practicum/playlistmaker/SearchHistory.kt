package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "key_for_search_history"

class SearchHistory(sharedPreferences: SharedPreferences) {

    val tracksSearchHistory: MutableList<Track> = ArrayList()
    val sharedPrefs = sharedPreferences

    init {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        if (json != null) {
            val savedSearchHistory = Gson().fromJson(json, Array<Track>::class.java)
            for (i in savedSearchHistory.indices) {
                tracksSearchHistory.add(savedSearchHistory[i])
            }
        }
    }

    fun addToSearchHistory(item: Track) {
        for (track in tracksSearchHistory) {
            if (track.trackId == item.trackId) {
                tracksSearchHistory.remove(track)
                break
            }
        }
        tracksSearchHistory.add(0, item)
        if (tracksSearchHistory.size > 10) tracksSearchHistory.removeAt(10)
    }

    fun deleteSearchHistory() {
        tracksSearchHistory.clear()
    }

    fun writeSearchHistory() {
        val json = Gson().toJson(tracksSearchHistory)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}
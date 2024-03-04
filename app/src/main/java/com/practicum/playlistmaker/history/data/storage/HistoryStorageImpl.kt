package com.practicum.playlistmaker.history.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.history.data.api.HistoryStorageApi
import com.practicum.playlistmaker.search.domain.models.Track


const val SEARCH_HISTORY_KEY = "key_for_search_history"

class HistoryStorageImpl(private val sharedPreferences: SharedPreferences) :
    HistoryStorageApi {

    override fun read(): Array<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)

        if (json != null) return Gson().fromJson(json, Array<Track>::class.java)

        return arrayOf()
    }

    override fun write(tracks: MutableList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

}
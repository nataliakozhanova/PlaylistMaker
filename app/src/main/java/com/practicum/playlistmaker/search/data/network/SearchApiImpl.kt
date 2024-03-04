package com.practicum.playlistmaker.search.data.network

import com.google.gson.GsonBuilder
import com.practicum.playlistmaker.util.CustomDateTypeAdapter
import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

class SearchApiImpl() : SearchApi {
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                    .create()
            )
        )
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun searchTracks(searchQuery: String): TracksSearchResponse {

        val response = iTunesService.search(searchQuery).execute()
        when (response.code()) {
            200 -> return response.body() ?: TracksSearchResponse(emptyList())
            else -> throw Exception("server error")
        }
    }
}



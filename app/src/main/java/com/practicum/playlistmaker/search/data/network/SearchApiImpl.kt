package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.api.SearchApi
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.models.NetworkException
import com.practicum.playlistmaker.search.domain.models.ServerErrorException

class SearchApiImpl(
    private val iTunesService: ITunesApi,
    private val context: Context) : SearchApi {

    override fun searchTracks(searchQuery: String): TracksSearchResponse {

        if(!isConnected()) {
            throw NetworkException()
        }

        val response = iTunesService.search(searchQuery).execute()
        when (response.code()) {
            200 -> return response.body() ?: TracksSearchResponse(emptyList())
            else -> throw ServerErrorException()
        }
    }
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}



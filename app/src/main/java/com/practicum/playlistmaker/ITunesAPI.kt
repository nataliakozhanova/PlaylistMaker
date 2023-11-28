package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class ITunesResponse(
    val resultCount: Int,
    val results: List<Track>
)

interface ITunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<ITunesResponse>
}
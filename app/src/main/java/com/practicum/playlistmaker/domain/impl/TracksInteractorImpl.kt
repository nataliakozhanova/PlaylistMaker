package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.SearchResult
import java.lang.Exception

class TracksInteractorImpl(private val repository : TracksRepository) : TracksInteractor {

    override fun getTrackListByName(expression: String, consumer: TracksInteractor.SearchResultConsumer) {

        val t = Thread {

            val res : SearchResult = try {
                SearchResult(repository.getTrackListByName(expression), false)
            } catch (e: Exception){
                SearchResult(emptyList(), true)
            }
            consumer.consume(res)
        }
        t.start()
    }

}
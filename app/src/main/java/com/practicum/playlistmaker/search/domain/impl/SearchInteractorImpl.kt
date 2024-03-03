package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.SearchResult
import java.lang.Exception

class SearchInteractorImpl(private val repository : SearchRepository) : SearchInteractor {

    override fun getTrackListByName(expression: String, consumer: SearchInteractor.SearchResultConsumer) {

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
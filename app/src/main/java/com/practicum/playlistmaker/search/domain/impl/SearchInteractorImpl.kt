package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val repository : SearchRepository) : SearchInteractor {

    override fun getTrackListByName(expression: String) : Flow<SearchResult> {

        return repository.getTrackListByName(expression)
            .map{ result -> SearchResult(result, false)}
            .catch { e -> emit(SearchResult(emptyList(), true)) }
    }

}
package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.SearchApi
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.SearchApiImpl
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getSearchApi(): SearchApi {

        return SearchApiImpl()
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(this.getSearchApi())
    }

    fun getTrackListByNameUseCase(): TracksInteractor {
        return TracksInteractorImpl(this.getTracksRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun getPlayerInteraktor() : PlayerInteractor {
        return PlayerInteractorImpl(this.getPlayerRepository())
    }

}
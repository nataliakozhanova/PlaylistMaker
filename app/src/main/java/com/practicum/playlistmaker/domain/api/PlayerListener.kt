package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface PlayerListener {
    fun onPlayerChange(state: PlayerState)
}
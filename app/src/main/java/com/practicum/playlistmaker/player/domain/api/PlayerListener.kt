package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface PlayerListener {
    fun onPlayerChange(state: PlayerState)
}
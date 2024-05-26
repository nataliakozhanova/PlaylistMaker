package com.practicum.playlistmaker.player.ui.models

sealed interface PlayerVMState{
    object Default : PlayerVMState
    object Prepared : PlayerVMState
    data class Playing (val elapsedTime: String) : PlayerVMState
    data class Pause(val elapsedTime: String) : PlayerVMState
}

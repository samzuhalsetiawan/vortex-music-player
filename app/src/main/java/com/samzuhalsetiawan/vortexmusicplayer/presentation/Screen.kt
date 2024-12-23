package com.samzuhalsetiawan.vortexmusicplayer.presentation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object Main : Screen()

    @Serializable
    data object MusicControl : Screen()

    @Serializable
    data object MusicList : Screen()

}
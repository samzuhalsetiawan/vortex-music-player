package com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen

import androidx.annotation.FloatRange
import com.samzuhalsetiawan.musicplayer.model.Music

data class MainScreenState(
    val musics: List<Music> = emptyList(),
    val nowPlaying: Music? = null,
    val isPlaying: Boolean = false,
    @FloatRange(0.0, 1.0) val volume: Float = 1f,
)

sealed class MainScreenEvent {
    data class PlayMusic(val music: Music) : MainScreenEvent()
    data object NextMusic : MainScreenEvent()
    data object PreviousMusic : MainScreenEvent()
    data object OnMusicPlayPauseButtonClick : MainScreenEvent()
    data class OnVolumeChange(@FloatRange(0.0, 1.0) val volume: Float) : MainScreenEvent()
}
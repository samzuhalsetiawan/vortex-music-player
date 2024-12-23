package com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import com.samzuhalsetiawan.musicplayer.MusicPlayer
import com.samzuhalsetiawan.musicplayer.MusicPlayerState
import com.samzuhalsetiawan.vortexmusicplayer.data.MusicRepository
import com.samzuhalsetiawan.vortexmusicplayer.data.RepositoryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musicPlayer: MusicPlayer,
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnMusicPlayPauseButtonClick -> {
                musicPlayer.toggle()
            }

            is MainScreenEvent.OnVolumeChange -> {
                musicPlayer.setVolume(event.volume)
            }

            is MainScreenEvent.NextMusic -> {
                musicPlayer.next()
            }

            is MainScreenEvent.PlayMusic -> {
                val playlist = _state.value.musics.toMutableList().apply {
                    remove(event.music)
                    add(0, event.music)
                }
                musicPlayer.play(*playlist.toTypedArray())
            }

            is MainScreenEvent.PreviousMusic -> {
                musicPlayer.previous()
            }
        }
    }

    init {
        viewModelScope.launch {
            launch(Dispatchers.Main) {
                musicPlayer.volumeAsFlow.collect { volume ->
                    _state.update {
                        it.copy(volume = volume)
                    }
                }
            }
            launch(Dispatchers.IO) {
                musicRepository.getAllMusic().collect { result ->
                    when (result) {
                        is RepositoryResult.Loading -> {}
                        is RepositoryResult.Error -> throw result.error
                        is RepositoryResult.Success -> {
                            _state.update { it.copy(musics = result.data) }
                        }
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Main) {
            launch {
                musicPlayer.currentPlayingAsFlow.collect { music ->
                    _state.update {
                        it.copy(nowPlaying = music)
                    }
                }
            }
            launch {
                musicPlayer.stateAsFlow.collect { playerState ->
                    when (playerState) {
                        MusicPlayerState.PLAYING -> {
                            _state.update {
                                it.copy(isPlaying = true)
                            }
                        }

                        else -> _state.update {
                            it.copy(isPlaying = false)
                        }
                    }
                }
            }
        }
    }

}

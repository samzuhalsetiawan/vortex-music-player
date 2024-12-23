package com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen.musiccontrolscreen.MusicControlScreen
import com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen.musiclistscreen.SongListScreen

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    val context = LocalContext.current
    Scaffold { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
        ) {
            val pagerState = rememberPagerState { 2 }
            Button(
                onClick = {
                    println(
                        """
                       Debug: State
                       state.musics: [${state.musics.size}]${state.musics.map { it.title }}
                       state.nowPlaying: ${state.nowPlaying}
                       state.isPlaying: ${state.isPlaying}
                       state.volume: ${state.volume}
                    """.trimIndent()
                    )
                }
            ) {
                Text("DebugState")
            }
            HorizontalPager(
                state = pagerState,
            ) {
                when (it) {
                    0 -> MusicControlScreen(
                        nowPlaying = state.nowPlaying,
                        isPlaying = state.isPlaying,
                        volume = state.volume,
                        onMusicPlayPauseButtonClick = { music ->
                            onEvent(MainScreenEvent.OnMusicPlayPauseButtonClick)
                        },
                        onVolumeChange = { volume ->
                            onEvent(MainScreenEvent.OnVolumeChange(volume))
                        },
                        onPreviousSongButtonClick = {
                            onEvent(MainScreenEvent.PreviousMusic)
                        },
                        onNextSongButtonClick = {
                            onEvent(MainScreenEvent.NextMusic)
                        }
                    )

                    1 -> SongListScreen(
                        musics = state.musics,
                        nowPlaying = state.nowPlaying,
                        isPlaying = state.isPlaying,
                        onMusicPlayPauseButtonClick = { music ->
                            when {
                                state.nowPlaying == music -> {
                                    onEvent(MainScreenEvent.OnMusicPlayPauseButtonClick)
                                }

                                else -> {
                                    onEvent(MainScreenEvent.PlayMusic(music))
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
package com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen.musiclistscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.samzuhalsetiawan.musicplayer.model.Music
import com.samzuhalsetiawan.vortexmusicplayer.presentation.ui.theme.VortexMusicPlayerTheme
import com.samzuhalsetiawan.vortexmusicplayer.R

@Composable
fun SongListScreen(
    musics: List<Music> = emptyList(),
    nowPlaying: Music? = null,
    isPlaying: Boolean = false,
    onMusicPlayPauseButtonClick: (Music) -> Unit = {},
) {
    Column(
        modifier = Modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = musics,
                key = { it.id }
            ) { music ->
                ListItem(
                    headlineContent = {
                        Text(text = music.title ?: "Unknown")
                    },
                    supportingContent = {
                        Text(text = music.artist ?: "Unknown")
                    },
                    trailingContent = {
                        when {
                            nowPlaying == music && isPlaying -> {
                                IconButton(
                                    onClick = {
                                        onMusicPlayPauseButtonClick(music)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_pause),
                                        contentDescription = "Pause Button"
                                    )
                                }
                            }

                            else -> {
                                IconButton(
                                    onClick = {
                                        onMusicPlayPauseButtonClick(music)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Button"
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SongListScreenPreview() {
    VortexMusicPlayerTheme {
        SongListScreen(
            musics = emptyList()
        )
    }
}
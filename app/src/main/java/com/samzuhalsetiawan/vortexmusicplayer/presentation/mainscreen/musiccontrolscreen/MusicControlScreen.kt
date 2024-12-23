package com.samzuhalsetiawan.vortexmusicplayer.presentation.mainscreen.musiccontrolscreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samzuhalsetiawan.musicplayer.model.Music
import com.samzuhalsetiawan.vortexmusicplayer.presentation.ui.theme.VortexMusicPlayerTheme
import com.samzuhalsetiawan.vortexmusicplayer.R

@Composable
fun MusicControlScreen(
    nowPlaying: Music? = null,
    isPlaying: Boolean = false,
    volume: Float = 1f,
    onMusicPlayPauseButtonClick: (Music) -> Unit = {},
    onVolumeChange: (Float) -> Unit = {},
    onPreviousSongButtonClick: () -> Unit = {},
    onNextSongButtonClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        Row {
            Text(text = "Vortex")
        }
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Song Title")
            Text(text = "Artist")
        }
        Row(
            modifier = Modifier.fillMaxHeight(0.4f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clickable {
                        onPreviousSongButtonClick()
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Song Button"
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable {
                        nowPlaying?.let { onMusicPlayPauseButtonClick(nowPlaying) }
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    painter = if (isPlaying) {
                        painterResource(R.drawable.ic_pause)
                    } else {
                        painterResource(R.drawable.ic_play)
                    },
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = if (isPlaying) "Pause Button" else "Play Button"
                )
            }
            Box(
                modifier = Modifier
                    .clickable {
                        onNextSongButtonClick()
                    }
            ) {
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(8.dp),
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "Next Song Button"
                )
            }
        }
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
        )
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    VortexMusicPlayerTheme {
        MusicControlScreen()
    }
}
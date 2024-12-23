package com.samzuhalsetiawan.musicplayer.model

import android.net.Uri

data class Music(
    val id: Long,
    val title: String?,
    val artist: String?,
    val album: Album?,
    val duration: Long,
    val uri: Uri
)
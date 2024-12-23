package com.samzuhalsetiawan.vortexmusicplayer.data.util

import android.net.Uri
import com.samzuhalsetiawan.localstorage.AudioMetadata
import com.samzuhalsetiawan.localstorage.File
import com.samzuhalsetiawan.musicplayer.model.Album
import com.samzuhalsetiawan.musicplayer.model.Music

fun getMusicRequiredMetadata(): List<AudioMetadata> {
    return listOf(
        AudioMetadata.ID,
        AudioMetadata.DISPLAY_NAME,
        AudioMetadata.URI,
        AudioMetadata.TITLE,
        AudioMetadata.ARTIST,
        AudioMetadata.DURATION,
        AudioMetadata.ALBUM_ID,
        AudioMetadata.ALBUM_ART_URI,
    )
}

fun File<AudioMetadata>.toMusic(): Music {
    return Music(
        id = metadata[AudioMetadata.ID] as Long,
        title = metadata[AudioMetadata.TITLE] as? String,
        artist = metadata[AudioMetadata.ARTIST] as? String,
        album = Album(
            id = metadata[AudioMetadata.ALBUM_ID] as Long,
            albumArtUri = metadata[AudioMetadata.ALBUM_ART_URI] as? Uri,
        ),
        duration = metadata[AudioMetadata.DURATION] as Long,
        uri = metadata[AudioMetadata.URI] as Uri,
    )
}
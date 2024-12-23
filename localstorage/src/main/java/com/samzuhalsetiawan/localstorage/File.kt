package com.samzuhalsetiawan.localstorage

import android.net.Uri

class File<T : Metadata>(
    val metadata: HashMap<T, Any?>,
    val mimeType: String?,
    val uri: Uri
)

sealed interface Metadata

enum class AudioMetadata : Metadata {
    ID,
    DISPLAY_NAME,
    URI,
    TITLE,
    ARTIST,
    ABSOLUTE_FILESYSTEM_PATH,
    DURATION,
    ALBUM_ID,
    ALBUM_ART_URI
}

enum class AudioType {
    MUSIC
}
package com.samzuhalsetiawan.localstorage.util

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.database.getStringOrNull
import com.samzuhalsetiawan.localstorage.AudioMetadata
import com.samzuhalsetiawan.localstorage.File

internal fun List<AudioMetadata>.toMediaStoreProjection(): Array<String> {
    /*
    *  ID and DISPLAY_NAME are always required in order to get uri and mimeType,
    *  for construct File object
    *  at [Cursor.toAudioFile(ContentResolver, List<AudioMetadata>)]
     */
    val projection = mutableSetOf<String>(
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME
    )
    forEach { metadata ->
        val columnName = when (metadata) {
            AudioMetadata.ALBUM_ART_URI -> {
                MediaStore.Audio.AudioColumns.ALBUM_ID
            }

            AudioMetadata.URI -> {
                MediaStore.Audio.AudioColumns._ID
            }

            else -> metadata.toMediaStoreColumnName()
        }
        projection.add(columnName)
    }
    return projection.toTypedArray()
}

internal fun AudioMetadata.toMediaStoreColumnName(): String {
    return when (this) {
        AudioMetadata.ID -> MediaStore.Audio.AudioColumns._ID
        AudioMetadata.DISPLAY_NAME -> MediaStore.Audio.AudioColumns.DISPLAY_NAME
        AudioMetadata.TITLE -> MediaStore.Audio.AudioColumns.TITLE
        AudioMetadata.ARTIST -> MediaStore.Audio.AudioColumns.ARTIST
        AudioMetadata.ABSOLUTE_FILESYSTEM_PATH -> MediaStore.Audio.AudioColumns.DATA
        AudioMetadata.DURATION -> MediaStore.Audio.AudioColumns.DURATION
        AudioMetadata.ALBUM_ID -> MediaStore.Audio.AudioColumns.ALBUM_ID
        AudioMetadata.ALBUM_ART_URI -> {
            throw MetadataToColumnNameNotMatchException(name)
        }

        AudioMetadata.URI -> {
            throw MetadataToColumnNameNotMatchException(name)
        }
    }
}

internal fun Cursor?.forEachRecord(action: (cursor: Cursor) -> Unit) {
    this?.use {
        while (moveToNext()) {
            action(this)
        }
    }
}

internal fun Cursor.toAudioFile(
    contentResolver: ContentResolver,
    metadata: List<AudioMetadata>
): File<AudioMetadata> {
    val hashMap = HashMap<AudioMetadata, Any?>()
    for (key in metadata) {
        val column: Int? = try {
            getColumnIndexOrThrow(key.toMediaStoreColumnName())
        } catch (_: MetadataToColumnNameNotMatchException) {
            null
        }
        val value: Any? = when (key) {
            AudioMetadata.ID -> getLong(column!!)
            AudioMetadata.DISPLAY_NAME -> getString(column!!)
            AudioMetadata.TITLE -> getStringOrNull(column!!)
            AudioMetadata.ARTIST -> getStringOrNull(column!!)
            AudioMetadata.ABSOLUTE_FILESYSTEM_PATH -> getString(column!!)
            AudioMetadata.DURATION -> getLong(column!!)
            AudioMetadata.ALBUM_ID -> getLong(column!!)
            AudioMetadata.ALBUM_ART_URI -> ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID))
            )

            AudioMetadata.URI -> ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID))
            )
        }
        hashMap.put(key, value)
    }
    val idColumn = getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
    val displayNameColumn = getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
    val uri =
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, getLong(idColumn))
    val displayName = getString(displayNameColumn)
    return File(
        metadata = hashMap,
        mimeType = getFileMimeType(contentResolver, uri, displayName),
        uri = uri,
    )
}

internal fun getFileMimeType(
    contentResolver: ContentResolver,
    uri: Uri,
    displayName: String
): String? {
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        contentResolver.getType(uri)
    } else {
        val fileExtension = displayName.substringAfterLast(".", "").ifBlank { return null }
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
    }
}
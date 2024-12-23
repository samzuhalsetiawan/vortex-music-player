package com.samzuhalsetiawan.localstorage

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.samzuhalsetiawan.localstorage.MediaProvider.SortingOrder
import com.samzuhalsetiawan.localstorage.util.MetadataToColumnNameNotMatchException
import com.samzuhalsetiawan.localstorage.util.forEachRecord
import com.samzuhalsetiawan.localstorage.util.toAudioFile
import com.samzuhalsetiawan.localstorage.util.toMediaStoreColumnName
import com.samzuhalsetiawan.localstorage.util.toMediaStoreProjection

interface MediaProvider {
    enum class SortingOrder {
        ASCENDING,
        DESCENDING
    }

    suspend fun queryAudioFiles(
        projection: List<AudioMetadata>,
        filterByType: AudioType? = null,
        sortBy: Pair<AudioMetadata, SortingOrder>? = null
    ): List<File<AudioMetadata>>
}

internal class MediaProviderImpl(
    private val applicationContext: Context
) : MediaProvider {
    private val contentResolver = applicationContext.contentResolver
    override suspend fun queryAudioFiles(
        metadata: List<AudioMetadata>,
        filterByType: AudioType?,
        sortBy: Pair<AudioMetadata, SortingOrder>?
    ): List<File<AudioMetadata>> {
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = metadata.toMediaStoreProjection()
        val selectionClause = when (filterByType) {
            AudioType.MUSIC -> "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"
            else -> null
        }
        val selectionArgs = when (filterByType) {
            AudioType.MUSIC -> arrayOf("1")
            else -> null
        }
        val sortOrder = sortBy?.let {
            val columnName = try {
                it.first.toMediaStoreColumnName()
            } catch (_: MetadataToColumnNameNotMatchException) {
                return@let null
            }
            val sortingOrder = when (it.second) {
                SortingOrder.ASCENDING -> "ASC"
                SortingOrder.DESCENDING -> "DESC"
            }
            "$columnName $sortingOrder"
        }
        val audioFiles = mutableListOf<File<AudioMetadata>>()
        contentResolver
            .query(contentUri, projection, selectionClause, selectionArgs, sortOrder)
            .forEachRecord { cursor ->
                audioFiles.add(cursor.toAudioFile(contentResolver, metadata))
            }
        return audioFiles
    }
}
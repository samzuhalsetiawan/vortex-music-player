package com.samzuhalsetiawan.vortexmusicplayer.data

import android.content.Context
import com.samzuhalsetiawan.localstorage.AudioMetadata
import com.samzuhalsetiawan.localstorage.AudioType
import com.samzuhalsetiawan.localstorage.LocalStorage
import com.samzuhalsetiawan.localstorage.MediaProvider.SortingOrder
import com.samzuhalsetiawan.musicplayer.model.Music
import com.samzuhalsetiawan.vortexmusicplayer.data.util.getMusicRequiredMetadata
import com.samzuhalsetiawan.vortexmusicplayer.data.util.toMusic
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val localStorage: LocalStorage,
) {

    fun getAllMusic(): Flow<RepositoryResult<List<Music>>> = flow {
        emit(RepositoryResult.Loading())
        val musics = localStorage.mediaProvider.queryAudioFiles(
            projection = getMusicRequiredMetadata(),
            filterByType = AudioType.MUSIC,
            sortBy = AudioMetadata.TITLE to SortingOrder.ASCENDING,
        ).map { it.toMusic() }
        emit(RepositoryResult.Success(musics))
    }.catch {
        emit(RepositoryResult.Error(it))
    }

}
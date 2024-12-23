package com.samzuhalsetiawan.vortexmusicplayer.di

import android.app.Application
import android.content.Context
import com.samzuhalsetiawan.localstorage.LocalStorage
import com.samzuhalsetiawan.musicplayer.MusicPlayer
import com.samzuhalsetiawan.vortexmusicplayer.data.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modules {

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideMusicPlayer(
        @ApplicationContext applicationContext: Context
    ): MusicPlayer {
        val coroutineScope = GlobalScope
        return MusicPlayer(applicationContext, coroutineScope)
    }

    @Provides
    @Singleton
    fun provideLocalStorage(
        @ApplicationContext applicationContext: Context
    ): LocalStorage {
        return LocalStorage(applicationContext)
    }

}
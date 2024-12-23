package com.samzuhalsetiawan.musicplayer

import android.content.Context
import android.util.Log
import androidx.annotation.FloatRange
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.samzuhalsetiawan.musicplayer.model.Music
import com.samzuhalsetiawan.musicplayer.model.Playlist
import com.samzuhalsetiawan.musicplayer.util.generateUniqueID
import com.samzuhalsetiawan.musicplayer.util.toMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class RepeatMode {
    OFF,
    SINGLE,
    PLAYLIST
}

enum class MusicPlayerState {
    IDLE,
    PLAYING,
    SHUTDOWN,
}

interface MusicPlayer {
    val state: MusicPlayerState
    val currentPlaying: Music?
    val currentPlaylist: Playlist
    val volume: Float
    val stateAsFlow: SharedFlow<MusicPlayerState>
    val currentPlayingAsFlow: SharedFlow<Music?>
    val currentPlaylistAsFlow: SharedFlow<Playlist>
    val volumeAsFlow: SharedFlow<Float>
    fun insertMusicIntoCurrentPlaylist(music: Music)
    fun insertMusicIntoCurrentPlaylist(vararg music: Music)
    fun removeMusicFromCurrentPlaylist(music: Music)
    fun play()
    fun play(vararg music: Music)
    fun next()
    fun previous()
    fun pause()
    fun toggle()
    fun setVolume(@FloatRange(0.0, 1.0) volume: Float)
    fun shutdown()
}

fun MusicPlayer(
    applicationContext: Context,
    coroutineScope: CoroutineScope
): MusicPlayer {
    return MusicPlayerImpl(applicationContext, coroutineScope)
}

internal class MusicPlayerImpl(
    applicationContext: Context,
    coroutineScope: CoroutineScope
) : MusicPlayer, Player.Listener {

    companion object {
        const val DEFAULT_INITIAL_VOLUME = 1f
        const val DEFAULT_PLAYLIST_NAME = "Playlist"
    }

    private var _coroutineScope: CoroutineScope?
    private var _player: ExoPlayer?
    private val _state: MutableStateFlow<MusicPlayerState>
    private val _currentPlaying: MutableStateFlow<Music?>
    private val _currentPlaylist: MutableStateFlow<Playlist>
    private val _volume: MutableStateFlow<Float>

    init {
        _coroutineScope = coroutineScope
        _player = ExoPlayer
            .Builder(applicationContext)
            .setAudioAttributes(AudioAttributes.DEFAULT, false)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .also { it.addListener(this) }
        _state = MutableStateFlow(MusicPlayerState.IDLE)
        _currentPlaying = MutableStateFlow(null)
        _currentPlaylist = MutableStateFlow(createDefaultPlaylist())
        _volume = MutableStateFlow(DEFAULT_INITIAL_VOLUME)
    }

    override fun shutdown() {
        _player?.let { player ->
            if (player.playbackState == Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
            player.removeListener(this)
            player.release()
        }
        _player = null
        _state.value = MusicPlayerState.SHUTDOWN
        _currentPlaying.value = null
        _currentPlaylist.value = createDefaultPlaylist()
        _volume.value = DEFAULT_INITIAL_VOLUME
        _coroutineScope?.coroutineContext?.cancel()
        _coroutineScope = null
    }

    override val state: MusicPlayerState = _state.value
    override val currentPlaying: Music? = _currentPlaying.value
    override val currentPlaylist: Playlist = _currentPlaylist.value
    override val volume: Float = _volume.value
    override val stateAsFlow: SharedFlow<MusicPlayerState> = _state.asSharedFlow()
    override val currentPlayingAsFlow: SharedFlow<Music?> = _currentPlaying.asSharedFlow()
    override val currentPlaylistAsFlow: SharedFlow<Playlist> = _currentPlaylist.asSharedFlow()
    override val volumeAsFlow: SharedFlow<Float> = _volume.asSharedFlow()

    init {
        _coroutineScope?.launch {
            _currentPlaylist.collect { playlist ->
                withContext(Dispatchers.Main) {
                    _player?.apply {
                        setMediaItems(playlist.musics.map { it.toMediaItem() })
                        prepare()
                    }
                }
            }
        }
    }

    //Player.Listener
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _state.update {
            if (isPlaying) MusicPlayerState.PLAYING else it
        }
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int
    ) {
        val music = mediaItem?.localConfiguration?.tag as? Music
        if (_currentPlaying.value != music) {
            _currentPlaying.update { music }
        }
    }
    //End Player.Listener

    override fun insertMusicIntoCurrentPlaylist(music: Music) {
        _currentPlaylist.update {
            it.copy(musics = it.musics + music)
        }
    }

    override fun insertMusicIntoCurrentPlaylist(vararg music: Music) {
        _currentPlaylist.update {
            it.copy(musics = it.musics + music)
        }
    }

    override fun removeMusicFromCurrentPlaylist(music: Music) {
        _currentPlaylist.update {
            it.copy(musics = it.musics - music)
        }
    }

    override fun play() {
        if (_currentPlaying.value == null) return
        _player?.play()
    }

    override fun play(vararg music: Music) {
        val newPlaylist = createDefaultPlaylist(*music)
        _currentPlaylist.update { newPlaylist }
        _player?.playWhenReady = true
    }

    override fun next() {
        _player?.apply {
            if (hasNextMediaItem()) {
                seekToNextMediaItem()
            }
        }
    }

    override fun previous() {
        _player?.apply {
            if (hasPreviousMediaItem()) {
                seekToPreviousMediaItem()
            }
        }
    }

    override fun pause() {
        val player = _player ?: return
        if (player.isPlaying) {
            player.pause()
            _state.update { MusicPlayerState.IDLE }
        }
    }

    override fun toggle() {
        when (_state.value) {
            MusicPlayerState.IDLE -> play()
            MusicPlayerState.PLAYING -> pause()
            MusicPlayerState.SHUTDOWN -> Unit
        }
    }

    override fun setVolume(volume: Float) {
        val player = _player ?: return
        player.volume = volume
        _volume.update { volume }
    }

    private fun createDefaultPlaylist(vararg music: Music): Playlist {
        return Playlist(
            id = generateUniqueID(),
            name = DEFAULT_PLAYLIST_NAME,
            musics = listOf(*music)
        )
    }
}
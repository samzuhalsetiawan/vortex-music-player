package com.samzuhalsetiawan.musicplayer.model

data class Playlist(
    val id: Long,
    val name: String,
    val musics: List<Music>
)
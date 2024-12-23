package com.samzuhalsetiawan.musicplayer.util

import androidx.media3.common.MediaItem
import com.samzuhalsetiawan.musicplayer.model.Music
import kotlin.random.Random

internal fun Music.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setTag(this)
        .setUri(uri)
        .build()
}

internal fun generateUniqueID(): Long {
    val timestamp = System.currentTimeMillis().toByteArray().sliceArray(2..7)
    val randomness = Random.nextBytes(2)
    val uid = timestamp + randomness
    return uid.toLong()
}

private fun Long.toByteArray(): ByteArray {
    val byteArray = ByteArray(Long.SIZE_BYTES)
    for (i in 0 until Long.SIZE_BYTES) {
        byteArray[i] = (this shr (Long.SIZE_BITS - ((1 + i) * Byte.SIZE_BITS))).toByte()
    }
    return byteArray
}

private fun ByteArray.toLong(): Long {
    var long = 0L
    for (i in lastIndex downTo 0) {
        long = long or ((this[i].toLong() and 0xFF) shl (Byte.SIZE_BITS * (lastIndex - i)))
    }
    return long
}

package com.samzuhalsetiawan.vortexmusicplayer.data

sealed class RepositoryResult<T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Error<T>(val error: Throwable) : RepositoryResult<T>()
    class Loading<T> : RepositoryResult<T>()
}
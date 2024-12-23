package com.samzuhalsetiawan.localstorage

import android.content.Context

class LocalStorage(
    applicationContext: Context
) {

    val mediaProvider: MediaProvider = MediaProviderImpl(applicationContext)

}
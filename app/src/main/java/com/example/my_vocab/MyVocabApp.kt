package com.example.my_vocab

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
@HiltAndroidApp
class MyVocabApp: Application() {

      companion object {
          lateinit var my_logger:DebugLogger
      }

    override fun onCreate() {
        super.onCreate()
        my_logger=DebugLogger()
        Timber.plant(my_logger)
    }
}
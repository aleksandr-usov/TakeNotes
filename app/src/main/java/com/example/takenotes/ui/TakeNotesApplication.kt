package com.example.takenotes.ui

import android.app.Application
import com.facebook.stetho.Stetho

class TakeNotesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}
package com.smartbiostream.presentation

import android.app.Application
import android.content.Context

/**
 * Main app
 */
class MyApp : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
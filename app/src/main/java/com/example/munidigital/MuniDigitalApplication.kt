package com.example.munidigital

import android.app.Application
import android.webkit.WebView

class MuniDigitalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        WebView.setWebContentsDebuggingEnabled(true)
    }
}

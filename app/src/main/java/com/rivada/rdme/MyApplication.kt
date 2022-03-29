package com.rivada.rdme

import android.app.Application
import android.util.Log
import com.rivada.rdme.utils.FileLog.open
import java.io.File


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val logFile = File(this.externalCacheDir!!.absolutePath, "/logs.txt")
        open(logFile, Log.VERBOSE, 1000000)
    }
}
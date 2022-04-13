package com.rivada.rdme

import android.app.Application
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.rivada.rdme.utils.FileLog.open
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MyApplication:Application() {
    private val filepath = "RDME_Logs"

    override fun onCreate() {
        super.onCreate()
        val logFile= File(getExternalFilesDir(filepath), "/logs.txt")
     /* val logFile=  File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/logs.txt" )*/
       // val logFile = File(this.externalCacheDir!!.absolutePath, "/logs.txt")
       open(logFile, Log.VERBOSE, 1000000)
    }
}
package com.rivada.rdme.utils

import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Sends Log output to a file
 * Created by volker on 06.02.15.
 */
object FileLog {
    private const val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    private const val MSG_FORMAT = "%s: %s - %s" // timestamp, tag, message
    private var sLogFilePath: String? = null
    private var sTheLogFile: File? = null
    private var sBufferedWriter: BufferedWriter? = null
    private var sCurrentPriority = 0
    private var sFileSizeLimit // bytes
            = 0

    fun open(logFilePath: File?, currentPriority: Int, fileSizeLimit: Int) {
      //  sLogFilePath = logFilePath
        sCurrentPriority = currentPriority
        sFileSizeLimit = fileSizeLimit
        sTheLogFile = logFilePath
        if (!sTheLogFile!!.exists()) {
            try {
                sTheLogFile!!.createNewFile()
            } catch (e: IOException) {
                Log.e("FileLog", Log.getStackTraceString(e))
            }
        }
        checkFileSize()
        try {
            sBufferedWriter = BufferedWriter(FileWriter(sTheLogFile, true))
        } catch (e: IOException) {
            Log.e("FileLog", Log.getStackTraceString(e))
        }
    }

    fun setCurrentPriority(currentPriority: Int) {
        sCurrentPriority = currentPriority
    }

    fun close() {
        try {
            if (sBufferedWriter != null) {
                sBufferedWriter!!.newLine()
                sBufferedWriter!!.flush()
                sBufferedWriter!!.close()
            }
        } catch (e: IOException) {
            Log.e("FileLog", Log.getStackTraceString(e))
        }
    }

    fun delete() {
        close()
        if (sTheLogFile != null) {
            sTheLogFile!!.delete()
        }
    }

    fun v(tag: String, msg: String): Int {
        writeToFile(Log.VERBOSE, tag, msg)
        return Log.v(tag, msg)
    }

    fun v(tag: String, msg: String, tr: Throwable?): Int {
        writeToFile(Log.VERBOSE, tag, msg, tr)
        return Log.v(tag, msg, tr)
    }

    fun d(tag: String, msg: String): Int {
        writeToFile(Log.DEBUG, tag, msg)
        return Log.d(tag, msg)
    }

    fun d(tag: String, msg: String, tr: Throwable?): Int {
        writeToFile(Log.DEBUG, tag, msg, tr)
        return Log.d(tag, msg, tr)
    }

    fun i(tag: String, msg: String): Int {
        writeToFile(Log.INFO, tag, msg)
        return Log.i(tag, msg)
    }

    fun i(tag: String, msg: String, tr: Throwable?): Int {
        writeToFile(Log.INFO, tag, msg, tr)
        return Log.i(tag, msg, tr)
    }

    fun w(tag: String, msg: String): Int {
        writeToFile(Log.WARN, tag, msg)
        return Log.w(tag, msg)
    }

    fun w(tag: String, msg: String, tr: Throwable?): Int {
        writeToFile(Log.WARN, tag, msg, tr)
        return Log.w(tag, msg, tr)
    }

    fun w(tag: String, tr: Throwable?): Int {
        writeToFile(Log.WARN, tag, "", tr)
        return Log.w(tag, tr)
    }

    fun e(tag: String, msg: String): Int {
        writeToFile(Log.ERROR, tag, msg)
        return Log.e(tag, msg)
    }

    fun e(tag: String, msg: String, tr: Throwable?): Int {
        writeToFile(Log.ERROR, tag, msg, tr)
        return Log.e(tag, msg, tr)
    }

    fun getStackTraceString(tr: Throwable?): String {
        return Log.getStackTraceString(tr)
    }

    private fun writeToFile(priority: Int, tag: String, msg: String, tr: Throwable? = null) {
        if (priority >= sCurrentPriority &&
            sBufferedWriter != null
        ) {
            try {
                if (checkFileSize()) {
                    sBufferedWriter = BufferedWriter(FileWriter(sTheLogFile, true))
                }
                sBufferedWriter!!.write(formatMsg(tag, msg))
                sBufferedWriter!!.newLine()
                if (tr != null) {
                    sBufferedWriter!!.write(Log.getStackTraceString(tr))
                    sBufferedWriter!!.newLine()
                }
                sBufferedWriter!!.flush()
            } catch (e: IOException) {
                Log.e("FileLog", Log.getStackTraceString(e))
            }
        }
        if (sBufferedWriter == null) {
            Log.e("FileLog", "You have to call FileLog.open(...) before starting to log")
        }
    }

    private fun formatMsg(tag: String, msg: String): String {
        return String.format(MSG_FORMAT, currentTimeStamp, tag, msg)
    }

    private val currentTimeStamp: String?
        private get() {
            var currentTimeStamp: String? = null
            try {
                val dateFormat = SimpleDateFormat(
                    TIMESTAMP_FORMAT,
                    Locale.getDefault()
                )
                currentTimeStamp = dateFormat.format(Date())
            } catch (e: Exception) {
                Log.e("FileLog", Log.getStackTraceString(e))
            }
            return currentTimeStamp
        }

    private fun checkFileSize(): Boolean {
        var createdNewLogFile = false
        try {
            if (sTheLogFile!!.length() > sFileSizeLimit) {
                val to = File(sLogFilePath + ".old")
                if (to.exists()) {
                    to.delete()
                }
                sTheLogFile!!.renameTo(to)
                sTheLogFile = File(sLogFilePath)
                sTheLogFile!!.createNewFile()
                createdNewLogFile = true
            }
        } catch (e: IOException) {
            Log.e("FileLog", Log.getStackTraceString(e))
        }
        return createdNewLogFile
    }
}
package com.rivada.rdme.utils

import android.content.Context
import android.content.SharedPreferences

class AppConstants(var context: Context) {
    var pref: SharedPreferences
    var edior: SharedPreferences.Editor
    var PRIVATE_MODE: Int = 0

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        edior = pref.edit()
    }

    companion object {
        const val SELECT_FILE = 1
        val PREF_NAME: String = "KotlinDemo"
        val IS_UPDATE: String = "isUpdate"
        val KEY_NAME: String = "cellname"
        val KEY_COLOR: String = "cellcolor"
        val KEY_URL: String = "url"
        val KEY_SHOW: String = "showVideo"

    }
    fun isUpdate(): Boolean {
        return pref.getBoolean(IS_UPDATE, false)
    }

    fun createUpdateSession(config: Boolean,name: String, color: String,uri:String,showvideo:String) {
        edior.putBoolean(IS_UPDATE, config)
        edior.putString(KEY_NAME, name)
        edior.putString(KEY_COLOR, color)
        edior.putString(KEY_URL, uri)
        edior.putString(KEY_SHOW, showvideo)
        edior.commit()
    }
    fun getcolor(): String? {
        return pref.getString(KEY_COLOR, "")
    }
    fun getvideo(): String? {
        return pref.getString(KEY_SHOW, "")
    }
    fun getcellname():String? {
        return pref.getString(KEY_NAME, "")
    }
    fun getURL(): String? {
        return pref.getString(KEY_URL, "")
    }
}


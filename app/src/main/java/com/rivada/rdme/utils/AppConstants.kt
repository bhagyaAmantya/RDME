package com.rivada.rdme.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rivada.rdme.model.Cell
import java.lang.reflect.Type
import java.util.ArrayList

class AppConstants(var context: Context) {
    var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    private var PRIVATE_MODE: Int = 0


    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    companion object {
        const val SELECT_FILE = 1
        const val PREF_NAME: String = "KotlinDemo"
        const val IS_UPDATE: String = "isUpdate"
        const val KEY_NAME: String = "cellname"
        const val KEY_COLOR: String = "cellcolor"
        const val KEY_URL: String = "url"
        const val KEY_SHOW: String = "showVideo"
        const val KEY_ID: String = "cellId"
        const val KEY_CELL_LIST: String = "CELL_LIST"

    }

    fun isUpdate(): Boolean {
        return pref.getBoolean(IS_UPDATE,false)
    }
    fun createUpdateSession(
        config: Boolean,
        name: String,
        color: String,
        uri: String,
        showVideo: String,
        cellId:String
    ) {
        editor.putBoolean(IS_UPDATE, config)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_COLOR, color)
        editor.putString(KEY_URL, uri)
        editor.putString(KEY_SHOW, showVideo)
        editor.putString(KEY_ID, cellId)
        editor.commit()
    }

    fun getColor(): String? {
        return pref.getString(KEY_COLOR, "")
    }

    fun getVideo(): String? {
        return pref.getString(KEY_SHOW, "")
    }

    fun getCellName(): String? {
        return pref.getString(KEY_NAME, "")
    }

    fun getURL(): String? {
        return pref.getString(KEY_URL, "")
    }
    fun getCellId():String?{
        return pref.getString(KEY_ID,"")
    }

    fun saveArrayList(list: List<Cell>, key: String?) {
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getArrayList(key: String?): List<Cell>? {
        val gson = Gson()
        val json: String? = pref.getString(key, null)
        val type: Type = object : TypeToken<List<Cell>?>() {}.type
        return gson.fromJson(json, type)
    }
}


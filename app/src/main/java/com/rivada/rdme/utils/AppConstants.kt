package com.rivada.rdme.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rivada.rdme.model.Cell
import com.rivada.rdme.model.Signalqualitycolors
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
        const val KEY_SHOW_CELL_ID: String = "showCellId"
        const val KEY_ID: String = "cellId"
        const val KEY_NETWORK: String = "network"
        const val KEY_NETWORK_COLOR: String = "networkcolor"
        const val KEY_CELL_LIST: String = "CELL_LIST"
        const val KEY_SIGNAL_COLORS: String = "signal_colors"

    }

    fun isUpdate(): Boolean {
        return pref.getBoolean(IS_UPDATE, false)
    }

    fun createUpdateSession(
        config: Boolean,
        name: String,
        color: String,
        uri: String,
        cellId: String,
        networkName: String,
        homeColor: String
    ) {
        editor.putBoolean(IS_UPDATE, config)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_COLOR, color)
        editor.putString(KEY_URL, uri)
        editor.putString(KEY_ID, cellId)
        editor.putString(KEY_NETWORK, networkName)
        editor.putString(KEY_NETWORK_COLOR, homeColor)
        editor.commit()
    }

    fun getColor(): String? {
        return pref.getString(KEY_COLOR, "")
    }

    fun setVideo(showVideo: Boolean) {
        editor.putBoolean(KEY_SHOW, showVideo)
        editor.commit()
    }

    fun setCellId(showCellID: Boolean) {
        editor.putBoolean(KEY_SHOW_CELL_ID, showCellID)
        editor.commit()
    }

    fun getVideo(): Boolean {
        return pref.getBoolean(KEY_SHOW, false)
    }

    fun getShowCellId(): Boolean {
        return pref.getBoolean(KEY_SHOW_CELL_ID, false)
    }

    fun getCellName(): String? {
        return pref.getString(KEY_NAME, "")
    }

    fun getURL(): String? {
        return pref.getString(KEY_URL, "")
    }

    fun getCellId(): String? {
        return pref.getString(KEY_ID, "")
    }

    fun getNetworkName(): String? {
        return pref.getString(KEY_NETWORK, "")
    }

    fun getNetworkColor(): String? {
        return pref.getString(KEY_NETWORK_COLOR, "")
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
    fun saveSignalColors(signalQualityColors: Signalqualitycolors,key:String?){
        val gson = Gson()
        val json: String = gson.toJson(signalQualityColors)
        editor.putString(key, json)
        editor.apply()
    }

    fun getSignalColors(key: String?):Signalqualitycolors?{
        val gson = Gson()
        val json: String? = pref.getString(key, null)
        val type: Type = object : TypeToken<Signalqualitycolors?>() {}.type
        return gson.fromJson(json, type)
    }

}


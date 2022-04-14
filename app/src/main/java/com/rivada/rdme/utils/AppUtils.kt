package com.rivada.rdme.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rivada.rdme.model.PayLoadModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException

fun Context.toast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}
fun readJSON(jsonFileString: String?) :PayLoadModel{
    Log.i("data", jsonFileString.toString())
    val gson = Gson()
    val listPersonType = object : TypeToken<PayLoadModel>() {}.type
    var jsonData: PayLoadModel = gson.fromJson(jsonFileString, listPersonType)
    jsonData.payload.cells.forEachIndexed { idx, cell ->
        Log.i(
            "data",
            "> Item $idx:\n$cell"
        )
    }
    return jsonData
}

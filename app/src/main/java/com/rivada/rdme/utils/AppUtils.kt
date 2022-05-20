package com.rivada.rdme.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rivada.rdme.model.Cell
import com.rivada.rdme.model.PayLoadModel
import com.rivada.rdme.model.SignalData
import com.rivada.rdme.model.Signalqualitycolors
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException

fun Context.toast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
fun signalStrengthCalculation(signalData: SignalData, signalQualityColors: Signalqualitycolors?): Cell? {
    var result: Cell ? = null
    var rsrp = signalData.getSsRsrp
    var rsrq = signalData.getSsRsrq
    var sinr = signalData.getSsSinr

    if (rsrp > -60 && rsrp <= -80 && rsrq > -9 && rsrq <= -11 &&
        sinr > 32 || sinr >= 28
    ) {
        result = Cell(cellname = "Excellent", color = signalQualityColors?.green.toString(), "")//green
    } else if (rsrp < -80 && rsrp >= -92 && rsrq < -11 && rsrq <= -14 &&
        sinr < 28 || sinr >= 23
    ) {
        result = Cell(cellname = "Good", color = signalQualityColors?.blue.toString(), "") //blue
    } else if (rsrp < -92 && rsrp <= -110 && rsrq < -14 && rsrq <= -17 &&
        sinr < 23 || sinr >= 15
    ) {
        result = Cell(cellname = "Fair", color = signalQualityColors?.yellow.toString(), "") //yellow
    } else if (rsrp < -110 && rsrq < -17 || sinr < 15) {
        result = Cell(cellname = "Poor", color = signalQualityColors?.red.toString(), "")//red
    } else {
        result = Cell(cellname = "Invalid value of parameter", color = signalQualityColors?.black.toString(), "")//black
    }
    return result

}

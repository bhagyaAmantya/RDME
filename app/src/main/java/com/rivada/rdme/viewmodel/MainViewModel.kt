package com.rivada.rdme.viewmodel

import android.telephony.SignalStrength
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rivada.rdme.model.*
import com.rivada.rdme.utils.signalStrengthCalculation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel () {
    private val mutableCID = MutableLiveData<CellInfo>()
    val cId: LiveData<CellInfo> get() = mutableCID

    private val mData = MutableLiveData<List<PersonItem>?>()
    val nData: MutableLiveData<List<PersonItem>?> get() = mData

    private val mPayLoad = MutableLiveData<PayLoadModel>()
    val nPayLoad: LiveData<PayLoadModel> get() = mPayLoad

    private val mSignalStrength = MutableLiveData<String>()
    val nSignalStrength: LiveData<String> get() = mSignalStrength

    private val mColorCode = MutableLiveData<String>()
    val nColorCode: LiveData<String> get() = mColorCode

    private val mConfigDialog = MutableLiveData<Boolean>()
    val nConfigDialog: LiveData<Boolean> get() = mConfigDialog

    private val mSignalData = MutableLiveData<SignalData>()
    val nSignalData: MutableLiveData<SignalData> get() = mSignalData

    fun cellInfoCID(item: CellInfo) {
        mutableCID.value = item
    }
    fun payLoadList(payLoad: PayLoadModel) {
        mPayLoad.value = payLoad
    }

    fun getSignal(signalStrength: String, colorCode: String) {
        mSignalStrength.value = signalStrength
        mColorCode.value = colorCode
    }
    fun updateSignalData(signalData: SignalData){
        mSignalData.value = signalData
        updateSignalColorCode(signalData)
    }

    private fun updateSignalColorCode(mSignalData: SignalData){
        val cell = signalStrengthCalculation(mSignalData)
        mSignalStrength.value = cell?.cellname
        mColorCode.value = cell?.color
    }
     fun updateConfigDialog(dialog:Boolean){
        mConfigDialog.value = dialog
    }

}
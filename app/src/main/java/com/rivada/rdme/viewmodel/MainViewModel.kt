package com.rivada.rdme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rivada.rdme.model.CellInfo


class MainViewModel: ViewModel () {
    private val mutableCID = MutableLiveData<CellInfo>()
    val cId: LiveData<CellInfo> get() = mutableCID

    fun cellInfoCID(item:CellInfo) {
        mutableCID.value = item
    }
}
package com.rivada.rdme.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.*
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rivada.rdme.R
import com.rivada.rdme.model.CellInfo
import com.rivada.rdme.utils.toast
import com.rivada.rdme.viewmodel.MainViewModel


class FiveGDetectionActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)
        title = "KotlinApp"
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@FiveGDetectionActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@FiveGDetectionActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@FiveGDetectionActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val allCellInfo = telephonyManager.allCellInfo
        for (cellInfo in allCellInfo) {
            if (cellInfo is CellInfoGsm) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(com.rivada.rdme.model.CellInfo("Gsm",cellIdentity.cid.toString()))
                //TODO Use cellIdentity to check MCC/MNC code, for instance.
            } else if (cellInfo is CellInfoWcdma) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(com.rivada.rdme.model.CellInfo(
                    "Wcdma",cellIdentity.cid.toString()
                ))
            } else if (cellInfo is CellInfoLte) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(com.rivada.rdme.model.CellInfo(
                    "Lte",cellIdentity.ci.toString())
                )
            } else if (cellInfo is CellInfoCdma) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(com.rivada.rdme.model.CellInfo(
                    "Cdma",cellIdentity.networkId.toString())
                )
                Log.d("Test", "Identinty" + cellIdentity.networkId)
            } else if (cellInfo is CellInfoNr) {
                Log.d("Test", "info" + cellInfo.cellIdentity.toString())
                viewModel.cellInfoCID(com.rivada.rdme.model.CellInfo(
                    "Nr",cellInfo.cellIdentity.toString())
                )
                val cellIdentity = cellInfo.cellIdentity
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@FiveGDetectionActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        this.toast(R.string.granted)
                        val telephonyManager =
                            getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                        telephonyManager.allCellInfo
                    }
                } else {
                    this.toast(R.string.denied)
                }
                return
            }
        }
    }
}
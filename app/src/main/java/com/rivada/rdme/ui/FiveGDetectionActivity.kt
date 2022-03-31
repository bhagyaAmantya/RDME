package com.rivada.rdme.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.*
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rivada.rdme.R
import com.rivada.rdme.utils.FileLog
import com.rivada.rdme.utils.toast
import com.rivada.rdme.viewmodel.MainViewModel


class FiveGDetectionActivity : AppCompatActivity() {
    private val TAG = "FiveGDetectionActivity"
    val MULTIPLE_PERMISSIONS = 10 // code you want.
    var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val viewModel: MainViewModel by viewModels()
    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            // permissions granted.
        } else {
            // show dialog informing them that we lack certain permissions
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)
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
        FileLog.v( TAG, allCellInfo.toString())
        for (cellInfo in allCellInfo) {
            if (cellInfo is CellInfoGsm) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(
                    com.rivada.rdme.model.CellInfo(
                        "Gsm",
                        cellIdentity.cid.toString()
                    )
                )
                FileLog.v( TAG, cellIdentity.toString())
            } else if (cellInfo is CellInfoWcdma) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(
                    com.rivada.rdme.model.CellInfo(
                        "Wcdma", cellIdentity.cid.toString()
                    )
                )
                FileLog.v( TAG, cellIdentity.toString())
              //  FileLog.saveFileUsingMediaStore(this, "sdgjsdsjd", "bc")

            } else if (cellInfo is CellInfoLte) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(
                    com.rivada.rdme.model.CellInfo(
                        "Lte", cellIdentity.ci.toString()
                    )
                )
                FileLog.v( TAG, cellIdentity.toString())
                //Toast.makeText(this, cellIdentity.ci, Toast.LENGTH_SHORT).show()

            } else if (cellInfo is CellInfoCdma) {
                val cellIdentity = cellInfo.cellIdentity
                viewModel.cellInfoCID(
                    com.rivada.rdme.model.CellInfo(
                        "Cdma", cellIdentity.networkId.toString()
                    )
                )
                FileLog.v( TAG, cellIdentity.toString())
                //Toast.makeText(this, cellIdentity.networkId, Toast.LENGTH_SHORT).show()
            } else if (cellInfo is CellInfoNr) {
                val cellIdentity = cellInfo.cellIdentity
                Log.d("Test", "info" + cellInfo.cellIdentity.toString())
                viewModel.cellInfoCID(
                    com.rivada.rdme.model.CellInfo(
                        "Nr", cellIdentity.toString()
                    )
                )
                FileLog.v( TAG, cellIdentity.toString())
                //Toast.makeText(this, cellIdentity.toString(), Toast.LENGTH_SHORT).show()
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
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    @JvmName("onRequestPermissionsResult1")
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>?,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                } else {
                    // no permissions granted.
                }
                return
            }
        }
    }
}
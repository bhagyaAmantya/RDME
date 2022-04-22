package com.rivada.rdme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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
import com.rivada.rdme.model.CellInfo
import com.rivada.rdme.model.PayLoadModel
import com.rivada.rdme.model.SignalData
import com.rivada.rdme.utils.*
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FiveGDetectionActivity"

@AndroidEntryPoint
class FiveGDetectionActivity : AppCompatActivity() {
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var mNetworkUtils: NetworkUtils
    lateinit var session: AppConstants

    val MULTIPLE_PERMISSIONS = 10
    var permissions = listOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        /* Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.WRITE_SETTINGS*/
    )
    private val viewModel: MainViewModel by viewModels()
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            managePermissions.checkPermissions()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        managePermissions = ManagePermissions(this, permissions, PermissionsRequestCode)
        session = AppConstants(this)
        internetConnection()
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
        try {
            val allCellInfo = telephonyManager.allCellInfo
            FileLog.v(TAG, allCellInfo.toString())

            for (cellInfo in allCellInfo) {
                when (cellInfo) {
                    is CellInfoGsm -> {
                        val cellIdentity = cellInfo.cellIdentity
                        viewModel.cellInfoCID(
                            CellInfo(
                                getString(R.string.gsm),
                                cellIdentity.cid.toString()
                            )
                        )
                        FileLog.v(TAG, cellIdentity.toString())
                        // checkStrength(cellInfo.cellSignalStrength.dbm)

                    }
                    is CellInfoWcdma -> {
                        val cellIdentity = cellInfo.cellIdentity
                        viewModel.cellInfoCID(
                            CellInfo(
                                getString(R.string.wcdma), cellIdentity.cid.toString()
                            )
                        )
                        FileLog.v(TAG, cellIdentity.toString())
                        // checkStrength(cellInfo.cellSignalStrength.dbm)

                    }
                    is CellInfoLte -> {
                        val cellIdentity = cellInfo.cellIdentity
                        viewModel.cellInfoCID(
                            CellInfo(
                                getString(R.string.lte), cellIdentity.ci.toString()
                            )
                        )
                        FileLog.v(TAG, cellIdentity.toString())
                        //checkStrength(cellInfo.cellSignalStrength.dbm)

                    }
                    is CellInfoCdma -> {
                        val cellIdentity = cellInfo.cellIdentity
                        viewModel.cellInfoCID(
                            CellInfo(
                                getString(R.string.cdma), cellIdentity.networkId.toString()
                            )
                        )
                        FileLog.v(TAG, cellIdentity.toString())
                        // checkStrength(cellInfo.cellSignalStrength.dbm)
                    }
                    is CellInfoNr -> {
                        FileLog.v(TAG, "Inside CellInfoNr")
                        try {
                            val cellIdentity = cellInfo.cellIdentity as CellIdentityNr
                            viewModel.cellInfoCID(
                                CellInfo(
                                    getString(R.string.nr), cellIdentity.nci.toString()
                                )
                            )
                            FileLog.v(TAG, cellIdentity.toString())
                            val signalStrength = cellInfo.cellSignalStrength as CellSignalStrengthNr
                            viewModel.updateSignalData(
                                SignalData(
                                    signalStrength.csiRsrp,
                                    signalStrength.csiRsrq,
                                    signalStrength.csiSinr,
                                    signalStrength.ssRsrp,
                                    signalStrength.ssRsrq,
                                    signalStrength.ssSinr
                                )
                            )
                            // checkStrength(signalStrength.csiRsrp)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            Toast.makeText(
                this@FiveGDetectionActivity,
                "Available:${network.networkHandle}",
                Toast.LENGTH_SHORT
            ).show()
            Log.i(TAG, network.networkHandle.toString())
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_METERED
            ) || networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED)
            Toast.makeText(this@FiveGDetectionActivity, "capability:$unmetered", Toast.LENGTH_SHORT)
                .show()


        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Toast.makeText(this@FiveGDetectionActivity, "Lost", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkStrength(dbm: Int) {
        when {
            dbm >= -80 -> {
                //Best signal
                viewModel.getSignal("Excellent $dbm", "#008000")
            }
            dbm < -81 && dbm > -90 -> {
                //Good signal
                viewModel.getSignal("Very Good $dbm", "#0000FF")
            }
            dbm < -91 && dbm > -100 -> {
                //medium signal
                viewModel.getSignal("Good $dbm", "#FFFF00")
            }
            dbm < -101 && dbm > -110 -> {
                //Very low signal
                viewModel.getSignal("Fair $dbm", "#FF0000")
            }
            dbm < -111 && dbm > -120 -> {
                //Very weak signal
                viewModel.getSignal("poor $dbm", "#FF0000")
            }
            dbm > -120 -> {
                //Too low signal
                viewModel.getSignal("No Signal $dbm", "#000000")
            }
        }
    }

    private fun internetConnection() {
        mNetworkUtils = NetworkUtils(application)
        mNetworkUtils.observe(this) { isNetworkAvailable ->
            when (isNetworkAvailable) {
                true -> this.toast(R.string.online)
                false -> this.toast(R.string.offline)
            }
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.SELECT_FILE) {
                val path = data?.data
                val jsonSelectedFile = path?.let { contentResolver.openInputStream(it) }
                val inputAsString = jsonSelectedFile?.bufferedReader().use { it?.readText() }
                val payLoadModel: PayLoadModel = readJSON(inputAsString)
                viewModel.payLoadList(payLoadModel)
                session.createUpdateSession(
                    config = true,
                    payLoadModel.payload.cells[1].cellname,
                    payLoadModel.payload.cells[1].color,
                    payLoadModel.payload.video.url, payLoadModel.payload.video.showvideo
                )
                Toast.makeText(this, "Json: $inputAsString", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
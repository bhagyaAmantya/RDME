package com.rivada.rdme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.*
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
import com.rivada.rdme.utils.AppConstants.Companion.KEY_CELL_LIST
import com.rivada.rdme.utils.AppConstants.Companion.KEY_SIGNAL_COLORS
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FiveGDetectionActivity"

@AndroidEntryPoint
class FiveGDetectionActivity : AppCompatActivity() {
    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private lateinit var mNetworkUtils: NetworkUtils
    private lateinit var telephonyManager: TelephonyManager
    lateinit var session: AppConstants
    lateinit var mainHandler: Handler
    var backPressedTime: Long = 0

    var permissions = listOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private val viewModel: MainViewModel by viewModels()
    private  var nRCellId:String? = null

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
        managePermissions = ManagePermissions(this, permissions, permissionsRequestCode)
        session = AppConstants(this)
        internetConnection()
        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)
        mainHandler = Handler(Looper.getMainLooper())
          getCellInfo()
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTextTask)
    }
    override fun onPause() {
        super.onPause()
       mainHandler.removeCallbacks(updateTextTask)
    }
    private fun getCellInfo() {
        try {
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
            telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                telephonyManager.requestCellInfoUpdate(
                    this.mainExecutor,
                    object : TelephonyManager.CellInfoCallback() {
                        override fun onCellInfo(allCellInfo: MutableList<android.telephony.CellInfo>) {
                            try {
                                for (cellInfo in allCellInfo) {
                                    when (cellInfo) {
                                        is CellInfoGsm -> {
                                            val cellIdentity = cellInfo.cellIdentity
                                          //  nRCellId = cellIdentity.cid.toString()

                                            viewModel.cellInfoCID(
                                                CellInfo(
                                                    getString(R.string.gsm),
                                                    cellIdentity.cid.toString()
                                                )
                                            )
                                            FileLog.v(TAG, cellIdentity.toString())

                                        }
                                        is CellInfoWcdma -> {
                                            val cellIdentity = cellInfo.cellIdentity
                                            viewModel.cellInfoCID(
                                                CellInfo(
                                                    getString(R.string.wcdma),
                                                    cellIdentity.cid.toString()
                                                )
                                            )
                                            FileLog.v(TAG, cellIdentity.toString())

                                        }
                                        is CellInfoLte -> {
                                            val cellIdentity = cellInfo.cellIdentity
                                           //nRCellId = cellIdentity.ci.toString()

                                            viewModel.cellInfoCID(
                                                CellInfo(
                                                    getString(R.string.lte),
                                                    cellIdentity.ci.toString()
                                                )
                                            )
                                            FileLog.v(TAG, cellIdentity.toString())
                                        }
                                        is CellInfoCdma -> {
                                            val cellIdentity = cellInfo.cellIdentity
                                            viewModel.cellInfoCID(
                                                CellInfo(
                                                    getString(R.string.cdma),
                                                    cellIdentity.networkId.toString()
                                                )
                                            )
                                            FileLog.v(TAG, cellIdentity.toString())
                                        }
                                        is CellInfoNr -> {
                                            FileLog.v(TAG, "Inside CellInfoNr")
                                            try {
                                                val cellIdentity =
                                                    cellInfo.cellIdentity as CellIdentityNr
                                                nRCellId = cellIdentity.nci.toString()
                                                viewModel.cellInfoCID(
                                                    CellInfo(
                                                        getString(R.string.nr),
                                                        cellIdentity.nci.toString()
                                                    )
                                                )
                                                FileLog.v(TAG, cellIdentity.toString())
                                                val signalStrength =
                                                    cellInfo.cellSignalStrength as CellSignalStrengthNr
                                                viewModel.updateSignalData(
                                                    SignalData(
                                                        signalStrength.ssRsrp,
                                                        signalStrength.ssRsrq,
                                                        signalStrength.ssSinr
                                                    )
                                                )
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

                    })
            }
        }
        catch (e:Exception){
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
                viewModel.updateConfigDialog(true)
                session.saveArrayList(payLoadModel.payload.cells,KEY_CELL_LIST)
                session.saveSignalColors(payLoadModel.payload.signalqualitycolors, KEY_SIGNAL_COLORS)
                for (cells in payLoadModel.payload.cells){
                    if (cells.id == nRCellId){
                        session.createUpdateSession(
                            config = true,
                            cells.cellname,
                            cells.color,
                            payLoadModel.payload.video.url,
                            cellId = cells.id,
                            networkName = payLoadModel.payload.home.networkname,
                            homeColor = payLoadModel.payload.home.color
                        )
                    }
                    else{
                        session.createUpdateSession(
                            config = true,
                           "",
                            "",
                            payLoadModel.payload.video.url,
                           "",
                            networkName = payLoadModel.payload.home.networkname,
                            homeColor = payLoadModel.payload.home.color

                        )
                    }
                }
            }
        }
    }
    private val updateTextTask = object : Runnable {
        override fun run() {
            getCellInfo()
            mainHandler.postDelayed(this, 2000)
        }
    }
    override fun onBackPressed() {
        if (backPressedTime + 2500 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            this.toast(R.string.exit_message)
        }
        backPressedTime = System.currentTimeMillis()
    }
}

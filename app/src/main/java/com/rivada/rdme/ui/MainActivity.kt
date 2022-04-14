package com.rivada.rdme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rivada.rdme.R
import com.rivada.rdme.model.PersonItem
import com.rivada.rdme.utils.*
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mNetworkUtils: NetworkUtils
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private val viewModel: MainViewModel by viewModels()
    var permissions = listOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        managePermissions = ManagePermissions(this,permissions,PermissionsRequestCode)
        managePermissions.checkPermissions()
       /* val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)*/
        internetConnection()
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
                val jsonSelectedFile = path?.let { contentResolver.openInputStream(it) };
                val inputAsString = jsonSelectedFile?.bufferedReader().use { it?.readText() }
                readJSON(inputAsString)
                Toast.makeText(this, "Json: $inputAsString", Toast.LENGTH_LONG).show()
                }
            }
            }
    private fun readJSON(jsonFileString: String?) {
        Log.i("data", jsonFileString.toString())
        val gson = Gson()
        val listPersonType = object : TypeToken<List<PersonItem>>() {}.type
        var persons: List<PersonItem> = gson.fromJson(jsonFileString, listPersonType)
      //  viewModel.jsonData(persons)
        persons.forEachIndexed { idx, personItem -> Log.i("data", "> Item $idx:\n$personItem") }    }
}








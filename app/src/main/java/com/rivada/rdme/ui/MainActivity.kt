package com.rivada.rdme.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rivada.rdme.R
import com.rivada.rdme.utils.NetworkUtils
import com.rivada.rdme.utils.toast

class MainActivity : AppCompatActivity() {

    private lateinit var mNetworkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = this.findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        navView.setupWithNavController(navController)
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
}
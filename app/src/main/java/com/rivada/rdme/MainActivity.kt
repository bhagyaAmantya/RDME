package com.rivada.rdme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rivada.rdme.utils.NetworkUtils
import com.rivada.rdme.utils.toast

class MainActivity : AppCompatActivity() {

    private lateinit var mNetworkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
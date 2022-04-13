package com.rivada.rdme.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rivada.rdme.R
import com.rivada.rdme.model.PayLoadModel
import com.rivada.rdme.model.PersonItem
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        setUpVideoURL()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
        val backgroundImage: ImageView = findViewById(R.id.splashScreenImage)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_slide)
        backgroundImage.startAnimation(slideAnimation)

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, FiveGDetectionActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)
    }
    private fun setUpVideoURL() {
        val myJson = """
{
  "payload": {
    "cells": [
      {
        "id": "1",
        "color": "#FFFF00",
        "cellname": "Test cell 1"
      },
      {
        "id": "2",
        "color": "#0000",
        "cellname": "Test cell 2"
      }
    ],
    "video": {
      "showvideo": "false",
      "url": "https://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism/Manifest",
      "lowUrl": "url",
      "highUrl": "url",
      "description": "some description"
    }
  }
}
""".trimIndent()
        val gson = Gson()
        val listPersonType = object : TypeToken<PayLoadModel>() {}.type
        var jsonData: PayLoadModel = gson.fromJson(myJson, listPersonType)
        jsonData.payload.cells.forEachIndexed { idx, cell -> Log.i("data",
            "> Item $idx:\n$cell") }
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("jsonurl",jsonData.payload.video.url)
        editor.putString("show",jsonData.payload.video.showvideo)
        editor.putString("colour",jsonData.payload.cells[0].color)
        editor.putString("id",jsonData.payload.cells[0].id)
        editor.putString("name",jsonData.payload.cells[0].cellname)
        editor.apply()
    }




}
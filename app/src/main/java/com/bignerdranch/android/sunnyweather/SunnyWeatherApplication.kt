package com.bignerdranch.android.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication: Application() {
    
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
        const val TOKEN = "XqSR4M6Bo6zmzvtl"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
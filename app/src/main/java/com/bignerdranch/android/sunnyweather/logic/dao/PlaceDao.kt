package com.bignerdranch.android.sunnyweather.logic.dao

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.bignerdranch.android.sunnyweather.SunnyWeatherApplication
import com.bignerdranch.android.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {

    fun savePlace(place: Place) {
        Log.d("PlaceDao", "savePlace = ")
        sharedPreferences().edit().apply {
            Log.d("PlaceDao", "savePlace = putString")
            putString("place", Gson().toJson(place))
            apply()
        }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        Log.d("PlaceDao", "placeJson = $placeJson")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context.
            getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}
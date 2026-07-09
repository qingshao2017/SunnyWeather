package com.bignerdranch.android.sunnyweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.bignerdranch.android.sunnyweather.logic.Repository
import com.bignerdranch.android.sunnyweather.logic.model.Location

class WeatherViewModel: ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = 0.0
    var locationLat = 0.0
    var placeName = ""

    val weatherLiveData = locationLiveData.switchMap { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: Double, lat: Double) {
        locationLiveData.value = Location(lng, lat)
    }
}
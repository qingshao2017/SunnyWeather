package com.bignerdranch.android.sunnyweather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.bignerdranch.android.sunnyweather.logic.dao.PlaceDao
import com.bignerdranch.android.sunnyweather.logic.model.Place
import com.bignerdranch.android.sunnyweather.logic.model.Weather
import com.bignerdranch.android.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        Log.d("Repository", "searchPlaces")
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        Log.d("Repository", "searchPlaces ${placeResponse.status}")
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: Double, lat: Double) = fire(Dispatchers.IO) {
        Log.d("REQ_COUNT", "发起天气请求 lng=$lng lat=$lat")
        coroutineScope {
            val dailyResponse = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }.await()
            delay(1000)
            val realtimeResponse = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }.await()


            Log.d("Repository", "realtimeResponse.status = ${realtimeResponse.status}" +
                    "dailyResponse.status = ${dailyResponse.status}")

            return@coroutineScope if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(
                    realtimeResponse.result.realtime,
                    dailyResponse.result.daily
                )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response is ${dailyResponse.status}"
                    )
                )
            }
        }
    }


//    // 1. fire 返回 LiveData<Result<Weather>>
//    fun refreshWeather() = fire(Dispatchers.IO) { ... }
//
//    // 2. ViewModel 持有这个 LiveData
//    val weatherLiveData = refreshWeather(lng, lat)
//
//// 3. Fragment 观察
//    weatherLiveData.observe(viewLifecycleOwner) { result ->
//        // 这里收到的 result 就是 emit(result) 发出来的值
//    }
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}
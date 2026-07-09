package com.bignerdranch.android.sunnyweather.logic.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()
    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun searchPlaces(query: String) = placeService
        .searchPlaces(query).await()
    suspend fun getDailyWeather(lng:Double, lat:Double) = weatherService
        .getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng:Double, lat:Double) = weatherService
        .getRealtimeWeather(lng, lat).await()

    private suspend fun <T> Call<T>.await():T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val message = response.errorBody()?.string() ?: "无错误信息"
                    val body = response.body()
                    Log.d("SunnyWeatherNetwork", "body is $message response = $response")
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
package com.bignerdranch.android.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.Date


data class DailyResponse(val status: String, val location: List<Double>, val result:Result) {

    data class Result(val daily:Daily)

    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>,
                     @SerializedName("life_index") val lifeIndex: LifeIndex)

    data class Temperature(val date: String, val max:Float, val min:Float, val avg: Float)

    data class Skycon(val date: String, val value: String)

    data class LifeIndex(val ultraviolet: List<Ultraviolet>, val carWashing: List<CarWashing>,
        val dressing: List<Dressing>, val comfort: List<Comfort>, val coldRisk: List<ColdRisk>)

    data class Ultraviolet(val date: String, val index: String, val desc: String)
    data class CarWashing(val date: String, val index: String, val desc: String)
    data class Dressing(val date: String, val index: String, val desc: String)
    data class Comfort(val date: String, val index: String, val desc: String)
    data class ColdRisk(val date: String, val index: String, val desc: String)
}

//data class DailyResponse(val status: String, val result: Result) {
//
//    data class Result(val daily:Daily)
//
//    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>,
//        @SerializedName("life_index") val lifeIndex: LifeIndex)
//
//    data class Temperature(val max: Float, val min: Float)
//
//    data class Skycon(val value:String, val date: String)
//
//    data class LifeIndex(val coldRisk: List<LifeDescription>, val carWashing:
//    List<LifeDescription>, val ultraviolet: List<LifeDescription>, val dressing:
//    List<LifeDescription>)
//
//    data class LifeDescription(val desc: String)
//}
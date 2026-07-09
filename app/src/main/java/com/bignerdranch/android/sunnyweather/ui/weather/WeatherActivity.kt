package com.bignerdranch.android.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bignerdranch.android.sunnyweather.R
import com.bignerdranch.android.sunnyweather.logic.model.Weather
import com.bignerdranch.android.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java)}

    private lateinit var placeName:TextView
    private lateinit var currentTemp:TextView
    private lateinit var currentSky:TextView
    private lateinit var currentAQI:TextView
    private lateinit var nowLayout:RelativeLayout
    private lateinit var forecastLayout:LinearLayout
    private lateinit var coldRiskText:TextView
    private lateinit var dressingText:TextView
    private lateinit var ultravioletText:TextView
    private lateinit var carWashingText:TextView
    private lateinit var weatherLayout:ScrollView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var navBtn:Button
    lateinit var drawerLayout:DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather)
        weatherLayout = findViewById(R.id.weatherLayout)
        placeName = findViewById(R.id.placeName)
        currentTemp = findViewById(R.id.currentTemp)
        currentSky = findViewById(R.id.currentSky)
        currentAQI = findViewById(R.id.currentAQI)
        nowLayout = findViewById(R.id.nowLayout)
        forecastLayout = findViewById(R.id.forecastLayout)
        coldRiskText = findViewById(R.id.coldRiskText)
        dressingText = findViewById(R.id.dressingText)
        ultravioletText = findViewById(R.id.ultravioletText)
        carWashingText = findViewById(R.id.carWashingText)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        navBtn = findViewById(R.id.navBtn)
        drawerLayout = findViewById(R.id.drawerLayout)

        viewModel.locationLng = intent.getDoubleExtra("location_lng", 0.0)
        viewModel.locationLat = intent.getDoubleExtra("location_lat", 0.0)


        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name")?:""
        }

        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefreshLayout.isRefreshing = false
        })

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefreshLayout.setOnRefreshListener {
            refreshWeather()
        }
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefreshLayout.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        val currentTempText = "${realtime.temperature.toInt()}°C"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info

        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout, false)
            val dateInfo: TextView = view.findViewById(R.id.dateInfo)
            val skyIcon:ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo:TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo:TextView = view.findViewById(R.id.temperatureInfo)
//            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = skycon.date.substring(0, 10)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}°C"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        val lifeIndex = daily.lifeIndex
        Log.d("WeatherActivity", "lifeIndex = $lifeIndex")
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}
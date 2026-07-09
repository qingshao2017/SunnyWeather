package com.bignerdranch.android.sunnyweather.ui.place

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.sunnyweather.MainActivity
import com.bignerdranch.android.sunnyweather.R
import com.bignerdranch.android.sunnyweather.ui.weather.WeatherActivity

class PlaceFragment:Fragment() {

    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }

    private lateinit var adapter:PlaceAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var searchPlaceEdit:EditText
    private lateinit var bgImageView:ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_place, container, false)
        searchPlaceEdit = view.findViewById(R.id.searchPlaceEdit)
        bgImageView = view.findViewById(R.id.bgImageView)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("PlaceFragment", "isPlaceSaved = ${viewModel.isPlaceSaved()}")
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.placeLiveData.observe(this) {result ->
            val places = result.getOrNull()
            Log.d("PlaceFragment", "places = $places")
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }

    }
}
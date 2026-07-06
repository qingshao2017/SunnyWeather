package com.bignerdranch.android.sunnyweather.ui.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.bignerdranch.android.sunnyweather.logic.Repository
import com.bignerdranch.android.sunnyweather.logic.model.Place
import retrofit2.http.Query

class PlaceViewModel: ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()
    val placeLiveData: LiveData<Result<List<Place>>> = searchLiveData.switchMap { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
}
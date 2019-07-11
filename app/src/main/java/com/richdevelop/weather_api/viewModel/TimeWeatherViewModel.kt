package com.richdevelop.weather_api.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.richdevelop.weather_api.repository.TimeWeatherRepository
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import javax.inject.Inject

class TimeWeatherViewModel @Inject constructor(
    //savedStateHandle: SavedStateHandle,
    timeWeatherRepository: TimeWeatherRepository
) : ViewModel() {
    //val timeWeatherID: Int = savedStateHandle["tid"] ?: throw IllegalArgumentException("missing timeWeatherID id")
    val timeWeather: LiveData<TimeWeather?> = timeWeatherRepository.getTimeWeather()
}
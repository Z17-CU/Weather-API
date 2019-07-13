package com.richdevelop.weather_api.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.richdevelop.weather_api.repository.TimeWeatherRepository
import com.richdevelop.weather_api.repository.room.AppDataBase

class TimeWeatherViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <TimeWeatherViewModel : ViewModel?> create(modelClass: Class<TimeWeatherViewModel>): TimeWeatherViewModel {

        return TimeWeatherViewModel(
            TimeWeatherRepository(
                AppDataBase.instance(context).dao(),
                context
            )
        ) as TimeWeatherViewModel
    }
}
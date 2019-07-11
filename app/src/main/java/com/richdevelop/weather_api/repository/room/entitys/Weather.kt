package com.richdevelop.weather_api.repository.room.entitys

import androidx.room.TypeConverters
import com.richdevelop.weather_api.repository.room.converters.Converter

data class Weather(
    @TypeConverters(Converter::class)
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)
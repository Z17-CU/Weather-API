package com.richdevelop.weather_api.repository.room.entitys

import androidx.room.TypeConverters
import com.richdevelop.weather_api.repository.room.converters.Converter

data class Main(
    @TypeConverters(Converter::class)
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)
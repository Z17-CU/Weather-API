package com.richdevelop.weather_api.repository.room.entitys

import androidx.room.TypeConverters
import com.richdevelop.weather_api.repository.room.converters.Converter

data class Sys(
    @TypeConverters(Converter::class)
    val country: String,
    val id: Int,
    val message: Double,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)
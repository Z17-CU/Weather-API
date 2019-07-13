package com.richdevelop.weather_api.repository.room.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeWeather(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    @PrimaryKey
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
) {
    companion object {
        const val TABLE_NAME = "TimeWeather"
    }
}
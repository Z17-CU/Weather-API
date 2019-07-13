package com.richdevelop.weather_api.repository.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.richdevelop.weather_api.repository.room.entitys.*
import kotlin.collections.ArrayList

class Converter {
    private val gson = Gson()

    @TypeConverter
    fun stringToClouds(data: String?): Clouds {
        if (data == null) {
            return Clouds(0)
        }

        val listType = object : TypeToken<Clouds>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun cloudsToString(someObjects: Clouds): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToCoorddata(data: String?): Coord {
        if (data == null) {
            return Coord(0.0, 0.0)
        }

        val listType = object : TypeToken<Coord>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun coordToString(someObjects: Coord): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToMain(data: String?): Main {
        if (data == null) {
            return Main(0, 0, 0.0, 0.0, 0.0)
        }
        val listType = object : TypeToken<Main>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mainToString(someObjects: Main): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToSys(data: String?): Sys {
        if (data == null) {
            return Sys("", 0, 0.0, 0, 0, 0)
        }
        val listType = object : TypeToken<Sys>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun sysToString(someObjects: Sys): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToWeather(data: String?): List<Weather?> {
        if (data == null) {
            var list: List<Weather?> = ArrayList()
            list =  list + Weather("", "", 0, "")
            return list
        }
        val listType = object : TypeToken<List<Weather?>>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun weatherToString(someObjects: List<Weather?>): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToWind(data: String?): Wind {
        if (data == null) {
            return Wind(0.0)
        }
        val listType = object : TypeToken<Wind>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun windToString(someObjects: Wind): String {
        return gson.toJson(someObjects)
    }
}
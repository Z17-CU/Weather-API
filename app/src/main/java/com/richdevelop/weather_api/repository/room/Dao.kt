package com.richdevelop.weather_api.repository.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather


@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimeWeather(timeWeather: TimeWeather)

    @Query("SELECT * FROM ${TimeWeather.TABLE_NAME}")
    fun getTimeWeather(): LiveData<TimeWeather?>
}
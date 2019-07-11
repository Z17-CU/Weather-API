package com.richdevelop.weather_api.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.richdevelop.weather_api.repository.room.converters.Converter
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import com.richdevelop.weather_api.utils.Const.Companion.DATA_BASE_NAME

@Database(
    entities = [(TimeWeather::class)], version = 1
)
@TypeConverters(Converter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun dao(): Dao

    companion object {

        fun instance(context: Context) = Room
            .databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}

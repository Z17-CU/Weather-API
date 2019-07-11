package com.richdevelop.weather_api.repository.retrofit

import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET
    fun getTimeWeather(@Url url: String): Call<TimeWeather>

    companion object {
        private const val BASE_URL = "http://api.openweathermap.org"
        val apiService: APIService
            get() = RetrofitClient.getClient(BASE_URL)!!.create(APIService::class.java)
    }
}
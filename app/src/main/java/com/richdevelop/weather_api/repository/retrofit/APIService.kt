package com.richdevelop.weather_api.repository.retrofit

import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import com.richdevelop.weather_api.utils.Const.Companion.BASE_URL
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET
    fun getTimeWeather(@Url url: String): Call<TimeWeather>

    companion object {
        val apiServiceWather: APIService
            get() = RetrofitClient.get(BASE_URL)!!.create(APIService::class.java)

    }
}
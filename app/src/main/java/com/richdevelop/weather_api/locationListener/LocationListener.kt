package com.richdevelop.weather_api.locationListener

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Bundle
import com.richdevelop.weather_api.repository.retrofit.APIService
import com.richdevelop.weather_api.repository.room.Dao
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import retrofit2.Response
import java.util.*

internal class LocationListener(val context: Context, val dao: Dao, val apiService: APIService) : LocationListener {

    private val url =
        "/data/2.5/weather?APPID=aaff1a7a058627a71698a204d3fa78b7&units=metric&lang=" + Locale.getDefault().language

    override fun onLocationChanged(loc: Location) {

        val tempUrl = url + ("&lat=" + loc.latitude + "&lon=" + loc.longitude)

        val execute = @SuppressLint("StaticFieldLeak")

        object : AsyncTask<Void, Void, Void?>() {
            override fun doInBackground(vararg voids: Void?): Void? {

                val response: Response<TimeWeather>?
                try {
                    response = apiService.getTimeWeather(tempUrl).execute()
                    dao.insertTimeWeather(response.body()!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return null
            }
        }
        execute.execute()
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}

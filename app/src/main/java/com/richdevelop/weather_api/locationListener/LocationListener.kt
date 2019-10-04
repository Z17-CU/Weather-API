package com.richdevelop.weather_api.locationListener

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Bundle
import com.richdevelop.weather_api.repository.retrofit.APIService.Companion.apiServiceWather
import com.richdevelop.weather_api.repository.room.Dao
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import java.util.*


internal class LocationListener(val dao: Dao) : LocationListener {

    private val url =
        "/data/2.5/weather?APPID=aaff1a7a058627a71698a204d3fa78b7&units=metric&lang=" + Locale.getDefault().language

    override fun onLocationChanged(loc: Location) {

        val tempUrl = url + ("&lat=" + loc.latitude + "&lon=" + loc.longitude)

        val execute = @SuppressLint("StaticFieldLeak")

        object : AsyncTask<Void, Void, Void?>() {
            override fun doInBackground(vararg voids: Void?): Void? {

                val response: TimeWeather
                try {
                    response = apiServiceWather.getTimeWeather(tempUrl).execute().body()!!
                    response.id = 1
                    dao.insertTimeWeather(response)
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

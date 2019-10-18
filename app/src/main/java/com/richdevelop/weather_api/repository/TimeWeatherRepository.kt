package com.richdevelop.weather_api.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.richdevelop.weather_api.R
import com.richdevelop.weather_api.locationListener.LocationListener
import com.richdevelop.weather_api.repository.room.Dao
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TimeWeatherRepository @Inject constructor(
    private val dao: Dao,
    private val context: Context
) {

    fun getTimeWeather(): LiveData<TimeWeather?> {

        refreshTimeWeather()
        return dao.getTimeWeather()
    }

    private fun refreshTimeWeather() {
        // Runs in a background thread.

        getGPSLocationAndWeather()
    }

    private fun getGPSLocationAndWeather() {

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = LocationListener(dao)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.requiredLocationPermission),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 60000 * 15, 0f, locationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
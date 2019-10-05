package com.richdevelop.weather_api.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.richdevelop.weather_api.R
import com.richdevelop.weather_api.repository.retrofit.APIService
import com.richdevelop.weather_api.repository.room.AppDataBase
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import kotlinx.android.synthetic.main.layout_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay.INVERT_COLORS
import java.util.*


class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var layoutStaticLocation: LinearLayout
    private lateinit var fabLocation: FloatingActionButton

    var lastLocation: Location? = null

    //Start in center of Cuba
    private val startLatitude = 22.261369
    private val startLongitude = -79.577868

    private var autoCenter = true

    var mMarker: Marker? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Configuration.getInstance()
            .load(context, getDefaultSharedPreferences(context))
        val view = inflater.inflate(R.layout.layout_map, container, false)
        mapView = view.findViewById(R.id._mapView)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setCenter(GeoPoint(startLatitude, startLongitude, 10.0))
        mapView.controller.setZoom(6.5)
        mapView.maxZoomLevel = 20.toDouble()
        mapView.minZoomLevel = 3.toDouble()
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setMultiTouchControls(true)

        activity?.let {
            if (activity!!.resources.configuration.uiMode and
                UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
            ) {
                mapView.overlayManager.tilesOverlay.setColorFilter(INVERT_COLORS)
            }
        }

        val mReceive = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                mapView.controller.animateTo(p)
                return false
            }
        }
        val overlayEvents = MapEventsOverlay(mReceive)
        mapView.overlays.add(overlayEvents)
        mapView.invalidate()

        fabLocation = view.findViewById(R.id._fabLocation)
        fabLocation.setOnClickListener {
            if (lastLocation != null) {
                centerMap(lastLocation!!)
                autoCenter = true
            }
        }

        view.findViewById<ImageView>(R.id._imageViewChinchetaShadow).alpha = 0.5.toFloat()

        mapView.setOnTouchListener { _, event ->

            autoCenter = false
            if (lastLocation != null && fabLocation.visibility == View.GONE) {
                showFab()
            }
            updateTextViewCoordinates(mapView.mapCenter.latitude, mapView.mapCenter.longitude)

            if (event.action == MotionEvent.ACTION_DOWN) {
                _imageViewChincheta.animate()
                    .translationY((-_imageViewChincheta.height / 4).toFloat()).duration = 200

                _imageViewChinchetaShadow.animate()
                    .alpha(0.2.toFloat()).duration = 200
            } else if (event.action == MotionEvent.ACTION_UP) {
                _imageViewChincheta.animate()
                    .translationY(0.toFloat()).duration = 200

                _imageViewChinchetaShadow.animate()
                    .alpha(0.5.toFloat()).duration = 200
            }

            false
        }

        layoutStaticLocation = view.findViewById(R.id._layoutStaticLocation)
        layoutStaticLocation.setOnClickListener {

            /** Aqui return location */
            returnLocation()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //on back pressed
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                clearFullScreen()
                replaceFragment(TimeWeatherFragment())
                return@setOnKeyListener true
            }
            false
        }

        initLocationListener()

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    private fun centerMap(location: Location) {
        val point = GeoPoint(
            location.latitude,
            location.longitude,
            location.altitude
        )
        if (mapView.zoomLevelDouble < 18) {
            mapView.controller.animateTo(point, 18.toDouble(), 1000)
        } else {
            mapView.controller.animateTo(point)
        }
        updateTextViewCoordinates("Current location")
        hideFab()
    }

    private fun showFab() {
        try {
            fabLocation.visibility = View.VISIBLE
            fabLocation.animate()
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        fabLocation.visibility = View.VISIBLE
                    }
                })
                .alpha(100.toFloat()).duration = 300
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideFab() {
        try {
            fabLocation.animate()
                .alpha(0.toFloat())
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        fabLocation.visibility = View.GONE
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewCoordinates(latitude: Double, longitude: Double) {
        _textViewCoordenadas?.let {
            _textViewCoordenadas.text =
                "(${String.format("%.6f", latitude)}, ${String.format(
                    "%.6f",
                    longitude
                )})"
        }
        _progressBarLoading?.let {
            _progressBarLoading.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewCoordinates(text: String) {
        _textViewCoordenadas?.let {
            _textViewCoordenadas.text = "($text)"
        }
        _progressBarLoading?.let {
            _progressBarLoading.visibility = View.GONE
        }
    }

    private fun clearFullScreen() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @SuppressLint("MissingPermission")
    private fun initLocationListener() {
        val mLocationListener = object : LocationListener {
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onLocationChanged(location: Location) {

                _mapView?.let {
                    lastLocation = location

                    if (mMarker == null) {
                        mMarker = Marker(_mapView)
                        mMarker!!.setInfoWindow(null)
                        context?.let {
                            mMarker!!.icon =
                                ContextCompat.getDrawable(context!!, R.drawable.ic_my_position)
                        }
                        mMarker!!.position =
                            GeoPoint(location.latitude, location.longitude, location.altitude)
                        mMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    } else {
                        mapView.overlays.remove(mMarker)
                        mMarker!!.position =
                            GeoPoint(location.latitude, location.longitude, location.altitude)
                    }
                    mMarker!!.setOnMarkerClickListener { _, _ ->
                        centerMap(location)
                        hideFab()
                        false
                    }
                    mapView.overlays.add(mMarker)
                    mapView.invalidate()

                    if (autoCenter) {
                        centerMap(location)
                    } else {
                        showFab()
                    }
                }
            }
        }

        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            10f,
            mLocationListener
        )
    }

    /** Weather API */
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.layout_main, fragment)
            .commit()
    }

    private fun returnLocation() {
        context?.let {

            val url =
                "/data/2.5/weather?APPID=aaff1a7a058627a71698a204d3fa78b7&units=metric&lang=" + Locale.getDefault().language
            val tempUrl =
                url + ("&lat=" + mapView.mapCenter.latitude + "&lon=" + mapView.mapCenter.longitude)

            val execute = @SuppressLint("StaticFieldLeak")

            object : AsyncTask<Void, Void, Void?>() {
                override fun doInBackground(vararg voids: Void?): Void? {

                    val response: TimeWeather
                    try {
                        response =
                            APIService.apiServiceWather.getTimeWeather(tempUrl).execute().body()!!
                        response.id = 1
                        response.coord.lat = mapView.mapCenter.latitude
                        response.coord.lon = mapView.mapCenter.longitude
                        AppDataBase.getInstance(context!!).dao().insertTimeWeather(response)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    /** Exit */
                    clearFullScreen()
                    replaceFragment(TimeWeatherFragment())
                }
            }
            execute.execute()
        }
    }
}
package com.richdevelop.weather_api.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.richdevelop.weather_api.R
import kotlinx.android.synthetic.main.layout_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MapFragment : Fragment() {

    lateinit var mapView: MapView
    lateinit var layoutStaticLocation: LinearLayout
    lateinit var fabLocation: FloatingActionButton

    var lastLocation: Location? = null

    //Start in center of Cuba
    private val startLatitude = 22.261369
    private val startLongitude = -79.577868

    private var autoCenter = true

    var mMarker: Marker? = null

    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        val view = inflater.inflate(R.layout.layout_map, container, false)
        mapView = view.findViewById(R.id._mapView)
        val tileSource = XYTileSource(
            "HOT", 1, 10, 256, ".png",
            arrayOf(
                "http://a.tiles.redcuba.cu/",
                "http://b.tiles.redcuba.cu/",
                "http://c.tiles.redcuba.cu/"
            ), "© OpenStreetMap contributors"
        )

        //mapView.setTileSource(tileSource)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.animateTo(GeoPoint(startLatitude, startLongitude, 10.0))
        mapView.controller.setZoom(6.5)
        mapView.setBuiltInZoomControls(false)
        mapView.setMultiTouchControls(true)

        fabLocation = view.findViewById(R.id._fabLocation)
        fabLocation.setOnClickListener {
            if (lastLocation != null) {
                centerMap(lastLocation!!)
            }
        }

        mapView.setOnTouchListener { _, event ->

            autoCenter = false
            if (lastLocation != null && fabLocation.visibility == View.GONE) {
                showFab()
            }
            updateTextViewCoordinates(mapView.mapCenter.latitude, mapView.mapCenter.longitude)

            if (event.action == MotionEvent.ACTION_DOWN) {
                _imageViewChincheta.animate()
                    .translationY((-_imageViewChincheta.height / 4).toFloat()).duration = 300
            } else if (event.action == MotionEvent.ACTION_UP) {
                _imageViewChincheta.animate()
                    .translationY(0.toFloat()).duration = 300
            }

            false
        }

        layoutStaticLocation = view.findViewById(R.id._layoutStaticLocation)
        layoutStaticLocation.setOnClickListener {

            /** Aqui return location */
        }

        val mLocationListener = object : LocationListener {
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onLocationChanged(location: Location) {

                lastLocation = location

                if (mMarker == null) {
                    mMarker = Marker(mapView)
                    mMarker!!.setInfoWindow(null)
                    mMarker!!.setIcon(context!!.resources.getDrawable(R.drawable.ic_my_position))
                    mMarker!!.position =
                        GeoPoint(location.latitude, location.longitude, location.altitude)
                    mMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                } else {
                    mapView.overlays.remove(mMarker)
                    mMarker!!.position =
                        GeoPoint(location.latitude, location.longitude, location.altitude)
                }
                mMarker!!.setOnMarkerClickListener { _, _ ->
                    hideFab()
                    true
                }
                mapView.overlays.add(mMarker)

                if (autoCenter) {
                    centerMap(location)
                } else {
                    showFab()
                }
            }
        }

        val locationManager =
            context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            10000,
            10f,
            mLocationListener
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateTextViewCoordinates(startLatitude, startLongitude)
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
        if (mapView.zoomLevelDouble < 15) {
            mapView.controller.setZoom(15.toDouble())
        }
        mapView.controller.animateTo(
            GeoPoint(
                location.latitude,
                location.longitude,
                location.altitude
            )
        )
        updateTextViewCoordinates("Current location")
        hideFab()
    }

    private fun showFab() {
        fabLocation.visibility = View.VISIBLE
        fabLocation.animate()
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    fabLocation.visibility = View.VISIBLE
                }
            })
            .alpha(100.toFloat()).duration = 300
    }

    private fun hideFab() {
        fabLocation.animate()
            .alpha(0.toFloat())
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    fabLocation.visibility = View.GONE
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewCoordinates(latitude: Double, longitude: Double) {
        _textViewCoordenadas.text =
            "(${String.format("%.6f", latitude)}, ${String.format(
                "%.6f",
                longitude
            )})"
    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewCoordinates(text: String) {
        _textViewCoordenadas.text = "($text)"
    }
}
package com.richdevelop.weather_api.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.richdevelop.weather_api.R
import com.richdevelop.weather_api.utils.AccuracyOverlay
import kotlinx.android.synthetic.main.fragment_map.*
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportFragment
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay.INVERT_COLORS

/**
 * Created by richard on 4/10/19.
 */

/** To init like viewer
 *
val fragment = MapFragment()
val bundle = Bundle()
bundle.putDouble(MapFragment.START_LATITUDE, locationContent.lat)
bundle.putDouble(MapFragment.START_LONGITUDE, locationContent.lon)
bundle.putDouble(MapFragment.FRIEND_LATITUDE, locationContent.lat)
bundle.putDouble(MapFragment.FRIEND_LONGITUDE, locationContent.lon)
bundle.putDouble(MapFragment.START_ZOOM, locationContent.zoom)
bundle.putBoolean(MapFragment.START_LIKE_VIEWER, true)
bundle.putBoolean(MapFragment.AUTO_CENTER, false)
fragment.arguments = bundle
start(fragment)
 */

class MapFragment : SupportFragment() {

    companion object {
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val FRIEND_LATITUDE = "FRIEND_LATITUDE"
        const val FRIEND_LONGITUDE = "FRIEND_LONGITUDE"
        const val ZOOM = "ZOOM"
        const val START_LATITUDE = "START_LATITUDE"
        const val START_LONGITUDE = "START_LONGITUDE"
        const val START_ZOOM = "START_ZOOM"
        const val AUTO_CENTER = "AUTO_CENTER"
        const val LAST_LOCATION = "LAST_LOCATION"
        const val START_LIKE_VIEWER = "START_LIKE_VIEWER"
    }

    private lateinit var mapView: MapView
    private lateinit var layoutStaticLocation: LinearLayout
    private lateinit var fabLocation: FloatingActionButton

    private var lastLocation: Location? = null

    //Start in center of Cuba
    private var startLatitude = 22.261369
    private var startLongitude = -79.577868
    private var friendLatitude = 0.0
    private var friendLongitude = 0.0
    private var startZoom = 6.5

    private var startLikeViewer = false

    private var autoCenter = true

    private var mMarker: Marker? = null
    private var accuracyOverlay: AccuracyOverlay? = null

    private lateinit var locationManager: LocationManager
    private lateinit var mLocationListener: LocationListener

    @ColorInt
    var accuracyColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                context,
                context!!.resources.getString(R.string.requiredStoragePermission),
                Toast.LENGTH_SHORT
            ).show()
            return view
        }

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        arguments?.let {
            autoCenter = it.getBoolean(AUTO_CENTER, autoCenter)
            startLatitude = it.getDouble(START_LATITUDE, startLatitude)
            startLongitude = it.getDouble(START_LONGITUDE, startLongitude)
            startZoom = it.getDouble(START_ZOOM, startZoom)
            lastLocation = it.getParcelable(LAST_LOCATION)
            startLikeViewer = it.getBoolean(START_LIKE_VIEWER, startLikeViewer)
            friendLatitude = it.getDouble(FRIEND_LATITUDE, friendLatitude)
            friendLongitude = it.getDouble(FRIEND_LONGITUDE, friendLongitude)
        }

        initLocationListener()

        return onClickListeners(initView(inflater, container))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lastLocation?.let {
            paintMarker(lastLocation!!)
            updateTextViewCoordinates(lastLocation!!.latitude, lastLocation!!.longitude)
            if (!autoCenter) {
                showFab()
            }
        }
        if (startLikeViewer) {
            _imageViewChincheta.visibility = View.GONE
            _imageViewChinchetaShadow.visibility = View.GONE
            _layoutStaticLocation.visibility = View.GONE
            paintMarkerFriend(GeoPoint(friendLatitude, friendLongitude, 10.0))
        }
    }

    override fun onPause() {
        super.onPause()
        _mapView?.let { _mapView.onPause() }
        stopLocationListener()
    }

    override fun onResume() {
        super.onResume()
        _mapView?.let { _mapView.onResume() }
        startLocationListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        arguments?.let {
            arguments!!.putBoolean(AUTO_CENTER, autoCenter)
            _mapView?.let {
                arguments!!.putDouble(START_LATITUDE, _mapView.mapCenter.latitude)
                arguments!!.putDouble(START_LONGITUDE, _mapView.mapCenter.longitude)
                arguments!!.putDouble(START_ZOOM, _mapView.zoomLevelDouble)
            }
            arguments!!.putBoolean(START_LIKE_VIEWER, startLikeViewer)
            arguments!!.putParcelable(LAST_LOCATION, lastLocation)
            arguments!!.putDouble(FRIEND_LATITUDE, friendLatitude)
            arguments!!.putDouble(FRIEND_LONGITUDE, friendLongitude)
        }

        super.onSaveInstanceState(outState)
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        Configuration.getInstance()
            .load(context, getDefaultSharedPreferences(context))
        Configuration.getInstance().isDebugMode = true

        /** Custom tiles with authentication*/
        //Configuration.getInstance().additionalHttpRequestProperties["Authorization"] = "Bearer ${token}"
//        mapView.setTileSource(
//            XYTileSource(
//                "OSMPublicTransport", 6, 20, 256, ".png",
//                arrayOf("URL"), "© OpenStreetMap contributors")
//        )

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id._mapView)

        mapView.setTileSource(TileSourceFactory.MAPNIK)

        mapView.controller.setCenter(GeoPoint(startLatitude, startLongitude, 10.0))
        mapView.controller.setZoom(startZoom)
        mapView.maxZoomLevel = 20.0
        mapView.minZoomLevel = 3.0
        mapView.overlayManager.tilesOverlay.loadingBackgroundColor = Color.TRANSPARENT
        mapView.overlayManager.tilesOverlay.loadingLineColor = Color.TRANSPARENT
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setMultiTouchControls(true)

        activity?.let {
            if (activity!!.resources.configuration.uiMode and
                UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
            ) {
                mapView.overlayManager.tilesOverlay.setColorFilter(INVERT_COLORS)
            }
        }

        view.findViewById<ImageView>(R.id._imageViewChinchetaShadow).alpha = 0.5.toFloat()
        layoutStaticLocation = view.findViewById(R.id._layoutStaticLocation)
        fabLocation = view.findViewById(R.id._fabLocation)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onClickListeners(view: View): View {
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

        fabLocation.setOnClickListener {
            if (lastLocation != null) {
                centerMap(lastLocation!!)
                autoCenter = true
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

        view.isFocusableInTouchMode = true
        view.requestFocus()

        layoutStaticLocation.setOnClickListener {

            returnLocation()
        }
        return view
    }

    private fun centerMap(location: Location) {
        val point = GeoPoint(
            location.latitude,
            location.longitude,
            location.altitude
        )

        val zoom = when {
            location.accuracy < 100 -> 18
            location.accuracy < 500 -> 16
            location.accuracy < 1000 -> 14
            else -> 13
        }

        if (mapView.zoomLevelDouble < zoom) {
            mapView.controller.animateTo(point, zoom.toDouble(), 1000)
        } else {
            mapView.controller.animateTo(point)
        }
        updateTextViewCoordinates(
            "${resources.getString(R.string.currentLocation)} ${resources.getString(
                R.string.exact
            )} ${location.accuracy}${resources.getString(R.string.meters)}"
        )
        hideFab()
    }

    private fun showFab() {
        try {
            fabLocation.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideFab() {
        try {
            fabLocation.hide()
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
            _textViewCoordenadas.text = text
        }
        _progressBarLoading?.let {
            _progressBarLoading.visibility = View.GONE
        }
    }

    private fun clearFullScreen() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    private fun initLocationListener() {

        context?.let {

            mLocationListener = object : LocationListener {
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }

                override fun onLocationChanged(location: Location) {

                    if (location.provider == LocationManager.NETWORK_PROVIDER) {
                        if (lastLocation != null && location.accuracy > lastLocation!!.accuracy) {
                            return
                        }
                    }
                    _mapView?.let {

                        paintMarker(location)

                        if (autoCenter) {
                            centerMap(location)
                        } else {
                            showFab()
                        }
                    }
                }
            }

            locationManager =
                context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun startLocationListener() {

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                context,
                context!!.resources.getString(R.string.requiredLocationPermission),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                2f,
                mLocationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500,
                2f,
                mLocationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopLocationListener() {
        locationManager.removeUpdates(mLocationListener)
    }

    private fun paintMarker(location: Location) {
        lastLocation = location
        val point =
            GeoPoint(location.latitude, location.longitude, location.altitude)

        if (mMarker == null) {
            mMarker = Marker(_mapView)
            mMarker!!.setInfoWindow(null)
            context?.let {
                mMarker!!.icon =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_my_position)
            }
            mMarker!!.position = point
            mMarker!!.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        } else {
            mapView.overlays.remove(mMarker)
            mMarker!!.position = point
        }
        mMarker!!.setOnMarkerClickListener { _, _ ->
            centerMap(location)
            hideFab()
            false
        }

        if (accuracyOverlay != null) {
            mapView.overlays.remove(accuracyOverlay)
        } else {
            val typedValue = TypedValue()
            val theme = context!!.theme
            theme.resolveAttribute(R.attr.accuracyColor, typedValue, true)
            accuracyColor = typedValue.data
        }
        accuracyOverlay = AccuracyOverlay(
            point, location.accuracy, accuracyColor
        )
        mapView.overlays.add(accuracyOverlay)

        mapView.overlays.add(mMarker)
        mapView.invalidate()
    }

    private fun paintMarkerFriend(point: GeoPoint) {

        val mMarker = Marker(_mapView)
        mMarker.setInfoWindow(null)
        context?.let {
            mMarker.icon =
                ContextCompat.getDrawable(context!!, R.drawable.ic_location)
        }
        mMarker.position = point
        mMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        mapView.overlays.add(mMarker)
        mapView.invalidate()
    }

    private fun exit() {
        clearFullScreen()
        pop()
    }

    private fun returnLocation() {

        _mapView?.let {
            val bundle = Bundle()
            bundle.putDouble(LATITUDE, _mapView.mapCenter.latitude)
            bundle.putDouble(LONGITUDE, _mapView.mapCenter.longitude)
            bundle.putDouble(ZOOM, _mapView.zoomLevelDouble)
            setFragmentResult(ISupportFragment.RESULT_OK, bundle)
        }
        exit()
    }

    override fun onBackPressedSupport(): Boolean {
        clearFullScreen()
        return super.onBackPressedSupport()
    }
}
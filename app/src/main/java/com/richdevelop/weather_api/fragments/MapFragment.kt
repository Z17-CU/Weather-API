package com.richdevelop.weather_api.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.richdevelop.weather_api.R
import kotlinx.android.synthetic.main.layout_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.R.attr.visible
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.core.view.ViewCompat.animate
import android.annotation.SuppressLint
import android.os.AsyncTask
import org.osmdroid.api.IGeoPoint
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.events.MapListener
import org.osmdroid.events.DelayedMapListener
import android.view.*
import android.view.DragEvent
import android.view.View.OnDragListener
import android.widget.LinearLayout
import com.richdevelop.weather_api.repository.retrofit.APIService
import com.richdevelop.weather_api.repository.room.AppDataBase
import com.richdevelop.weather_api.repository.room.entitys.TimeWeather
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*
import kotlin.math.roundToLong


class MapFragment : Fragment() {

    lateinit var mapView: MapView
    lateinit var layoutStaticLocation: LinearLayout

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
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
        mapView.controller.animateTo(GeoPoint(23.138394, -82.469735, 10.0))
        mapView.controller.setZoom(10)
        mapView.setBuiltInZoomControls(false)
        mapView.setMultiTouchControls(true)

        mapView.setOnTouchListener { _, event ->

            updateTextViewCoordinates()

            if (event.action == MotionEvent.ACTION_DOWN) {
                _imageViewChincheta.animate()
                    .translationY((-_imageViewChincheta.height / 4).toFloat())
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {

                    })
            } else if (event.action == MotionEvent.ACTION_UP) {
                _imageViewChincheta.animate()
                    .translationY(0.toFloat())
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {

                    })
            }

            false
        }

        layoutStaticLocation = view.findViewById(R.id._layoutStaticLocation)
        layoutStaticLocation.setOnClickListener {

            borrar()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateTextViewCoordinates()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewCoordinates() {
        _textViewCoordenadas.text =
            "(${String.format("%.6f", mapView.mapCenter.latitude)}, ${String.format(
                "%.6f",
                mapView.mapCenter.longitude
            )})"
    }

    fun borrar() {
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
                    AppDataBase.instance(context!!).dao().insertTimeWeather(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return null
            }
        }
        execute.execute()
    }
}
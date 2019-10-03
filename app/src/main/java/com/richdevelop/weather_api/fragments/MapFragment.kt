package com.richdevelop.weather_api.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.richdevelop.weather_api.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapFragment : Fragment() {

    lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        val view = inflater.inflate(R.layout.layout_map, container, false)
        mapView = view.findViewById(R.id._mapView)
        val tileSource = XYTileSource("HOT", 1, 10, 256, ".png",
            arrayOf("http://a.tiles.redcuba.cu/", "http://b.tiles.redcuba.cu/", "http://c.tiles.redcuba.cu/"), "© OpenStreetMap contributors")

        //mapView.setTileSource(tileSource)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.animateTo(GeoPoint(23.138394,-82.469735, 10.0))
        mapView.controller.setZoom(10)
        mapView.setBuiltInZoomControls(false)
        mapView.setMultiTouchControls(true)

        return view
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
}
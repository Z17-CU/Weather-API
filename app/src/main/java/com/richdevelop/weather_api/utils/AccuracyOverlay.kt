package com.richdevelop.weather_api.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class AccuracyOverlay(private val location: GeoPoint?, accuracyInMeters: Float, accuracyColor: Int) : Overlay() {

    private var accuracyPaint: Paint? = Paint()
    private val screenCoords = Point()
    private var accuracy = 0f

    init {
        this.accuracy = accuracyInMeters
        this.accuracyPaint!!.strokeWidth = 2f
        this.accuracyPaint!!.color = accuracyColor
        this.accuracyPaint!!.isAntiAlias = true
    }

    override fun onDetach(view: MapView?) {
        accuracyPaint = null
    }

    override fun draw(c: Canvas, map: MapView, shadow: Boolean) {

        if (shadow) {
            return
        }

        if (location != null) {
            val pj = map.projection
            pj.toPixels(location, screenCoords)

            if (accuracy > 0) {  //Set this to a minimum pixel size to hide if accuracy high enough
                val accuracyRadius = pj.metersToEquatorPixels(accuracy)

                /* Draw the inner shadow. */
                accuracyPaint!!.isAntiAlias = false
                accuracyPaint!!.alpha = 30
                accuracyPaint!!.style = Paint.Style.FILL
                c.drawCircle(
                    screenCoords.x.toFloat(),
                    screenCoords.y.toFloat(),
                    accuracyRadius,
                    accuracyPaint!!
                )

                /* Draw the edge. */
                accuracyPaint!!.isAntiAlias = true
                accuracyPaint!!.alpha = 150
                accuracyPaint!!.style = Paint.Style.STROKE
                c.drawCircle(
                    screenCoords.x.toFloat(),
                    screenCoords.y.toFloat(),
                    accuracyRadius,
                    accuracyPaint!!
                )
            }
        }
    }
}
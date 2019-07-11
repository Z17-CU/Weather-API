package com.richdevelop.weather_api.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.richdevelop.weather_api.R
import com.richdevelop.weather_api.viewModel.TimeWeatherViewModel
import com.richdevelop.weather_api.viewModel.TimeWeatherViewModelFactory


class TimeWeatherFragment : Fragment() {

    private lateinit var viewModel: TimeWeatherViewModel

    private var textViewCity: TextView? = null
    private var textViewTemperature: TextView? = null
    private var textViewLowTemperature: TextView? = null
    private var textViewHighTemperature: TextView? = null
    private var textViewDescription: TextView? = null

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        textViewCity = view.findViewById<TextView>(R.id._textViewCity)
        textViewTemperature = view.findViewById<TextView>(R.id._textViewTemperature)
        textViewLowTemperature = view.findViewById<TextView>(R.id._textViewLowTemperature)
        textViewHighTemperature = view.findViewById<TextView>(R.id._textViewHighTemperature)
        textViewDescription = view.findViewById<TextView>(R.id._textViewDescription)

        val tempViewModel: TimeWeatherViewModel by viewModels(
            factoryProducer = { TimeWeatherViewModelFactory(context!!) }
        )
        viewModel = tempViewModel

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timeWeather.observe(this, Observer {
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
            if (it != null) {
                textViewCity!!.text = it.name
                textViewTemperature!!.text = (it.main.temp).toInt().toString() + resources.getString(R.string.gradosC)
                textViewLowTemperature!!.text = it.main.temp_min.toInt().toString() + resources.getString(R.string.gradosC)
                textViewHighTemperature!!.text = it.main.temp_max.toInt().toString() + resources.getString(R.string.gradosC)
                textViewDescription!!.text = it.weather[0].description.toUpperCase()
            } else {
                textViewCity!!.text = "- - -"
                textViewTemperature!!.text = "- -"
                textViewLowTemperature!!.text = "- -"
                textViewHighTemperature!!.text = "- -"
                textViewDescription!!.text = "- - -"
            }
        })
    }
}
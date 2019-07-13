package com.richdevelop.weather_api.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.richdevelop.weather_api.R
import com.richdevelop.weather_api.utils.Const.Companion.IMAGE_URL
import com.richdevelop.weather_api.utils.Const.Companion.IMAGE_URL_END
import com.richdevelop.weather_api.viewModel.TimeWeatherViewModel
import com.richdevelop.weather_api.viewModel.TimeWeatherViewModelFactory


class TimeWeatherFragment : Fragment() {

    private lateinit var viewModel: TimeWeatherViewModel

    private var textViewCity: TextView? = null
    private var textViewTemperature: TextView? = null
    private var textViewLowTemperature: TextView? = null
    private var textViewHighTemperature: TextView? = null
    private var textViewDescription: TextView? = null
    private var imageViewWeather: ImageView? = null
    var glideOptions: RequestOptions? = null
    var lastImage: String? = null


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
        imageViewWeather = view.findViewById<ImageView>(R.id._imageViewWeather)

        glideOptions = RequestOptions()
            .centerCrop()
            .error(null)

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
            if (it != null) {
                textViewCity!!.text = if (it.name == "La Luisa") {
                    "La Lisa"
                } else {
                    it.name
                }
                textViewTemperature!!.text = (it.main.temp).toInt().toString() + resources.getString(R.string.gradosC)
                textViewLowTemperature!!.text =
                    it.main.temp_min.toInt().toString() + resources.getString(R.string.grados)
                textViewHighTemperature!!.text =
                    it.main.temp_max.toInt().toString() + resources.getString(R.string.grados)
                textViewDescription!!.text = it.weather[0].description.toUpperCase()

                if (lastImage != it.weather[0].icon) {
                    loadImage(it.weather[0].icon)
                }

            } else {
                textViewCity!!.text = "- - -"
                textViewTemperature!!.text = "- -"
                textViewLowTemperature!!.text = "- -"
                textViewHighTemperature!!.text = "- -"
                textViewDescription!!.text = "- - -"
                imageViewWeather!!.setImageBitmap(null)
            }
        })
    }

    private fun loadImage(iconName: String) {
        val execute = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, Void?>() {


            var requestBuilder: RequestBuilder<*>? = null

            override fun onPostExecute(result: Void?) {

                requestBuilder!!.into(imageViewWeather!!)
                lastImage = iconName

                super.onPostExecute(result)
            }

            override fun doInBackground(vararg voids: Void?): Void? {

                requestBuilder = Glide.with(context!!)
                    .load(IMAGE_URL + iconName + IMAGE_URL_END)
                    .apply(glideOptions!!)
                    .apply(RequestOptions().override(100, 100))

                return null
            }
        }
        execute.execute()
    }
}
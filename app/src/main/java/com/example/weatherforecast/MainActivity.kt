package com.example.weatherforecast

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.weatherforecast.Model.WeatherForecastRoot
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var nameLocation: TextView
    private lateinit var myLocation: ImageView
    private lateinit var searchView: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var edtSearch: EditText
    private lateinit var layoutSearch: LinearLayout
    private lateinit var layoutBar: ConstraintLayout

    private lateinit var imgWeather: ImageView
    private lateinit var txtCelsius: TextView
    private lateinit var txtAstro: TextView
    private lateinit var txtHumidity: TextView
    private lateinit var txtWindSpeed: TextView

    private lateinit var context:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

    }

    private fun init(){
        nameLocation = findViewById(R.id.txt_location)
        myLocation = findViewById(R.id.my_location)
        searchView = findViewById(R.id.show_searchView)
        btnBack = findViewById(R.id.btn_back)
        edtSearch = findViewById(R.id.search_city)
        layoutSearch = findViewById(R.id.sreachView)
        layoutBar = findViewById(R.id.constraintLayout)

        imgWeather = findViewById(R.id.img_weather)
        txtCelsius = findViewById(R.id.txt_celsius)
        txtAstro = findViewById(R.id.txt_astro)
        txtHumidity = findViewById(R.id.txt_humidity)
        txtWindSpeed = findViewById(R.id.txt_windspeed)

        context = this
        getData(Constants.CURRENT_LOCATION)
        setupView()
    }

    private fun setupView() {
        searchView.setOnClickListener {
            layoutSearch.visibility = View.VISIBLE
            layoutBar.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            layoutSearch.visibility = View.GONE
            layoutBar.visibility = View.VISIBLE
        }

        myLocation.setOnClickListener {
            getData(Constants.CURRENT_LOCATION)
        }

        edtSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val myLocation = edtSearch.text.toString()
                getData(myLocation)
                edtSearch.clearFocus()
                edtSearch.text.clear()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                layoutSearch.visibility = View.GONE
                layoutBar.visibility = View.VISIBLE
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun getData(_myLocation: String) {
        val retrofitRequest = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.HTTP_WEATHER)
            .build()
            .create(Apinterface::class.java)

        val days = 1
        val aqi = "no"
        val alerts = "no"
        val retrofitData = retrofitRequest.getWeatherForecast(
            Constants.API_KEY,
            _myLocation,
            days,
            aqi,
            alerts
        )

        retrofitData.enqueue(object : Callback<WeatherForecastRoot?> {
            override fun onResponse(
                call: Call<WeatherForecastRoot?>,
                response: Response<WeatherForecastRoot?>
            ) {
                if (response.isSuccessful) {
                    val weatherData = response.body()

                    val name_Location = weatherData?.location?.name
                    val celsius = weatherData?.current?.temp_c
                    val humidity = weatherData?.current?.humidity
                    val windSpeed = weatherData?.current?.wind_mph
                    val condition = weatherData?.current?.condition?.text
                    val img_weather = weatherData?.current?.condition?.icon

                    Picasso.with(context).load("https:$img_weather").into(imgWeather)
                    txtCelsius.text = getString(R.string.text_celsius,celsius)
                    txtAstro.text = condition
                    txtHumidity.text = getString(R.string.text_humidity,humidity)
                    txtWindSpeed.text = getString(R.string.text_windSpeed,windSpeed)
                    nameLocation.text = name_Location
                }
            }

            override fun onFailure(call: Call<WeatherForecastRoot?>, t: Throwable) {
                t.message?.let { Log.e("onFailure", it) }
            }
        })
    }
}
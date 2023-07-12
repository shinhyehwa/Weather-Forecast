package com.example.weatherforecast

import android.provider.DocumentsContract.Root
import com.example.weatherforecast.Model.WeatherForecastRoot
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Apinterface {

    @GET(Constants.END_POINT)
    fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String,
    ): Call<WeatherForecastRoot>
}
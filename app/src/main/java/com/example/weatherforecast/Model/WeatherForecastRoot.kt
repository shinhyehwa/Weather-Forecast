package com.example.weatherforecast.Model

data class WeatherForecastRoot(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)
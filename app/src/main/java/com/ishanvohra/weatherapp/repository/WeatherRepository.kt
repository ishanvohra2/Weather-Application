package com.ishanvohra.weatherapp.repository

import com.ishanvohra.weatherapp.network.RetrofitClient

class WeatherRepository {

    private val baseUrl = "https://api.openweathermap.org/data/2.5/"

    suspend fun getCurrentWeather(lat: Double, long: Double, appId: String) = RetrofitClient(baseUrl).instance.getCurrentWeather(lat, long, appId)

    suspend fun getForecast(lat: Double, long: Double, appId: String) = RetrofitClient(baseUrl).instance.getForecast(lat, long, appId)
}
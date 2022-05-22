package com.ishanvohra.weatherapp.network

import com.ishanvohra.weatherapp.model.CurrentWeather
import com.ishanvohra.weatherapp.model.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat")lat: Double,
        @Query("lon")long: Double,
        @Query("appid")appId: String,
        @Query("units") units: String = "metric"
    ) : Response<CurrentWeather>

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat")lat: Double,
        @Query("lon")long: Double,
        @Query("appid")appId: String,
        @Query("units") units: String = "metric",
        @Query("cnt") cnt: Int = 6
    ) : Response<Forecast>

}
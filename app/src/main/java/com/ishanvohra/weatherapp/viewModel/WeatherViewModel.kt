package com.ishanvohra.weatherapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra.weatherapp.model.CurrentWeather
import com.ishanvohra.weatherapp.model.Forecast
import com.ishanvohra.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    val currentWeatherData = MutableStateFlow<CurrentWeather?>(null)
    val forecastData = MutableStateFlow<Forecast?>(null)
    val TAG = javaClass.simpleName

    fun getCurrentWeather(lat: Double, long: Double, appId: String) {
        viewModelScope.launch {
            val result = WeatherRepository().getCurrentWeather(lat, long, appId)
            if(result.isSuccessful) {
                Log.d(TAG, "getCurrentWeather: success")
                currentWeatherData.emit(result.body())
            }
            else {
                Log.d(TAG, "getCurrentWeather: failed ${result.code()}")
                currentWeatherData.emit(null)
            }
        }
    }

    fun getForecast(lat: Double, long: Double, appId: String){
        viewModelScope.launch {
            val result = WeatherRepository().getForecast(lat, long, appId)
            if(result.isSuccessful){
                Log.d(TAG, "getDailyForecast: success")
                forecastData.emit(result.body())
            }
            else{
                Log.d(TAG, "getDailyForecast: failed ${result.code()} ${result.message()}")
                forecastData.emit(null)
            }
        }
    }
}
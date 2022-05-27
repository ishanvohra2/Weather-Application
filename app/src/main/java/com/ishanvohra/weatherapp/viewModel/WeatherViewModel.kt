package com.ishanvohra.weatherapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra.weatherapp.model.CurrentWeather
import com.ishanvohra.weatherapp.model.Errors
import com.ishanvohra.weatherapp.model.Forecast
import com.ishanvohra.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    val currentWeatherUIState = MutableStateFlow<CurrentWeatherUIState>(CurrentWeatherUIState.CurrentWeatherLoadingState)
    val forecastState = MutableStateFlow<ForecastUIState>(ForecastUIState.ForecastLoadingState)
    val TAG = javaClass.simpleName

    /**
     * Fetch current weather conditions from network
     */
    fun getCurrentWeather(lat: Double, long: Double, appId: String) {
        viewModelScope.launch {
            val result = WeatherRepository().getCurrentWeather(lat, long, appId)
            if(result.isSuccessful) {
                Log.d(TAG, "getCurrentWeather: success")
                updateCurrentWeatherState(CurrentWeatherUIState.CurrentWeatherLoadedState(result.body()!!))
            }
            else {
                Log.d(TAG, "getCurrentWeather: failed ${result.code()}")
                updateCurrentWeatherState(CurrentWeatherUIState.CurrentWeatherErrorState(Errors.API_ERROR))
            }
        }
    }

    fun getForecast(lat: Double, long: Double, appId: String){
        viewModelScope.launch {
            val result = WeatherRepository().getForecast(lat, long, appId)
            if(result.isSuccessful){
                Log.d(TAG, "getDailyForecast: success")
                updateForecastState(ForecastUIState.ForecastLoadedState(result.body()!!))
            }
            else{
                Log.d(TAG, "getDailyForecast: failed ${result.code()} ${result.message()}")
                updateForecastState(ForecastUIState.ForecastErrorState(Errors.API_ERROR))
            }
        }
    }

    /**
     * Update current weather UI state flow
     */
    suspend fun updateCurrentWeatherState(state: CurrentWeatherUIState){
        currentWeatherUIState.emit(state)
    }

    /**
     * Update forecast Ui state flow
     */
    suspend fun updateForecastState(state: ForecastUIState){
        forecastState.emit(state)
    }

    sealed class CurrentWeatherUIState{
        object CurrentWeatherLoadingState: CurrentWeatherUIState()
        class CurrentWeatherLoadedState(val currentWeather: CurrentWeather): CurrentWeatherUIState()
        class CurrentWeatherErrorState(val error: Errors): CurrentWeatherUIState()
    }

    sealed class ForecastUIState{
        object ForecastLoadingState: ForecastUIState()
        class ForecastLoadedState(val forecast: Forecast): ForecastUIState()
        class ForecastErrorState(val error: Errors): ForecastUIState()
    }
}
package com.ishanvohra.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.ishanvohra.weatherapp.components.CurrentWeatherComponent
import com.ishanvohra.weatherapp.components.ForecastComponent
import com.ishanvohra.weatherapp.model.Errors
import com.ishanvohra.weatherapp.ui.theme.DarkBlue500
import com.ishanvohra.weatherapp.ui.theme.DarkBlue700
import com.ishanvohra.weatherapp.ui.theme.Purple700
import com.ishanvohra.weatherapp.ui.theme.WeatherAppTheme
import com.ishanvohra.weatherapp.viewModel.WeatherViewModel
import kotlinx.coroutines.launch

class WeatherActivity : ComponentActivity() {

    private var viewModel = WeatherViewModel()

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Purple700,
                ) {
                    MainLayout(viewModel)
                }
            }
        }
        checkForLocationPermissions()
    }

    private fun checkForLocationPermissions() {
        if(hasPermissions(this, PERMISSIONS)){
            initFusedLocationClientListener()
        }
        else{
            permReqLauncher.launch(PERMISSIONS)
        }
    }

    private fun initFusedLocationClientListener() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permReqLauncher.launch(PERMISSIONS)
            return
        }

        if(!isOnline()){
            lifecycleScope.launch {
                viewModel.updateCurrentWeatherState(WeatherViewModel.CurrentWeatherUIState.CurrentWeatherErrorState(Errors.NO_INTERNET))
                viewModel.updateForecastState(WeatherViewModel.ForecastUIState.ForecastErrorState(Errors.NO_INTERNET))
            }
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let{
                Log.d(javaClass.simpleName, "initFusedLocationClientListener: getting weather information")

                viewModel.getCurrentWeather(
                    it.latitude,
                    it.longitude,
                    getString(R.string.weather_api_key)
                )
                viewModel.getForecast(
                    it.latitude,
                    it.longitude,
                    getString(R.string.weather_api_key)
                )
            }
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                initFusedLocationClientListener()
            }
        }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    /**
     * Check if the device is online
     * If the device is not connected to internet, return false else return true
     */
    private fun isOnline(): Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
}

@Composable
fun MainLayout(viewModel: WeatherViewModel){
    Column(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .background(
                Brush.verticalGradient(
                    listOf(DarkBlue700, DarkBlue500)
                )
            ),
    ) {
        Text(
            text = "Weather Forecast",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(12.dp),
            color = Color.White
        )
        CurrentConditionsCard(viewModel = viewModel)
        Text(
            text = "3 hour forecast",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(12.dp),
            color = Color.White
        )
        ForecastList(viewModel = viewModel)
    }
}

@Composable
fun ForecastList(viewModel: WeatherViewModel) {
    when(val state = viewModel.forecastState.collectAsState().value){
        is WeatherViewModel.ForecastUIState.ForecastLoadedState -> ForecastComponent().ForecastListSuccess(
            foreCastData = state.forecast
        )
        is WeatherViewModel.ForecastUIState.ForecastLoadingState -> ForecastComponent().ForecastLoading()
        is WeatherViewModel.ForecastUIState.ForecastErrorState -> ForecastComponent().ForecastError(
            error = state.error
        )
    }

}

@Composable
fun CurrentConditionsCard(viewModel: WeatherViewModel) {
    when(val state = viewModel.currentWeatherUIState.collectAsState().value){
        is WeatherViewModel.CurrentWeatherUIState.CurrentWeatherLoadedState -> CurrentWeatherComponent().CurrentConditionsCardSuccess(
            value = state.currentWeather
        )
        is WeatherViewModel.CurrentWeatherUIState.CurrentWeatherLoadingState -> CurrentWeatherComponent().CurrentConditionsLoading()
        is WeatherViewModel.CurrentWeatherUIState.CurrentWeatherErrorState -> CurrentWeatherComponent().CurrentConditionsCardError(
            error = state.error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherAppTheme {
        MainLayout(WeatherViewModel())
    }
}
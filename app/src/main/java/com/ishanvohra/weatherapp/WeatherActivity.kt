package com.ishanvohra.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ishanvohra.weatherapp.model.CurrentWeather
import com.ishanvohra.weatherapp.model.Forecast
import com.ishanvohra.weatherapp.ui.theme.*
import com.ishanvohra.weatherapp.viewModel.WeatherViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private var currentWeatherData: CurrentWeather? by mutableStateOf(null)
private var forecastData: Forecast? by mutableStateOf(null)
private var viewModel = WeatherViewModel()
private var fusedlocationClient: FusedLocationProviderClient? = null

class WeatherActivity : ComponentActivity() {

    companion object {
        val TAG: String = WeatherActivity::class.java.simpleName
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Purple700,
                ) {
                    MainLayout()
                }
            }
        }
        fusedlocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkForLocationPermissions()
        initViewModel()
        collectWeatherData()
        collectForecastData()
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
        fusedlocationClient?.lastLocation?.addOnSuccessListener {
            it?.let{
                if (currentWeatherData == null) viewModel.getCurrentWeather(
                    it.latitude,
                    it.longitude,
                    getString(R.string.weather_api_key)
                )
                if (forecastData == null) viewModel.getForecast(
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

    private fun collectForecastData() {
        lifecycleScope.launch {
            viewModel.forecastData.collect{
                it?.let {
                    forecastData = it
                }
            }
        }
    }

    private fun collectWeatherData() {
        lifecycleScope.launch {
            viewModel.currentWeatherData.collect {
                it?.let {
                    currentWeatherData = it
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    }
}

@Composable
fun MainLayout(){
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
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(12.dp),
            color = Color.White
        )
        CurrentConditionsCard(currentWeatherData)
        Text(
            text = "3 hour forecast",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(12.dp),
            color = Color.White
        )
        ForecastList()
    }
}

@Composable
fun CurrentConditionsCard(value: CurrentWeather?) {
    Card(elevation = 2.dp,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = CardBackgroundGrey
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today",
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                    fontSize = 14.sp,
                    color = Color.White,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "${value?.main?.temp?.toInt()}",
                    fontSize = 60.sp,
                    color = Color.White,
                )
                Text(
                    text = "\u2103",
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(2.dp, 0.dp, 0.dp, 20.dp),
                    color = Color.Yellow,
                )
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("http://openweathermap.org/img/wn/${currentWeatherData?.weather?.get(0)?.icon}@2x.png")
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = "",
                    modifier = Modifier
                        .size(128.dp)
                        .weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_map_marker_outline),
                    contentDescription = ""
                )
                Text(
                    text = currentWeatherData?.name ?: "",
                    fontSize = 14.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun ForecastList(){
    LazyRow(
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        forecastData?.let {
            itemsIndexed(it.list){_: Int, item: Forecast.Item ->
                ForecastItem(item = item)
            }
        }
    }
}

@Composable
fun ForecastItem(item: Forecast.Item){
    Card(elevation = 2.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = CardBackgroundGrey
    ){
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("http://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png")
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .size(84.dp)
                    .padding(6.dp)
            )
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(item.dt_txt)
            Text(
                text = "${SimpleDateFormat("dd MMM", Locale.getDefault()).format(date!!)}," +
                        " ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)}",
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "${item.main.temp.toInt()}",
                    fontSize = 24.sp,
                    color = Color.White,
                )
                Text(
                    text = "\u2103",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(2.dp, 0.dp, 0.dp, 10.dp),
                    color = Color.Yellow,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherAppTheme {
        MainLayout()
    }
}
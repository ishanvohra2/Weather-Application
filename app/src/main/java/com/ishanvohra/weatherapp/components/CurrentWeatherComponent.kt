package com.ishanvohra.weatherapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ishanvohra.weatherapp.R
import com.ishanvohra.weatherapp.model.CurrentWeather
import com.ishanvohra.weatherapp.model.Errors
import com.ishanvohra.weatherapp.ui.theme.CardBackgroundGrey
import java.text.SimpleDateFormat
import java.util.*

class CurrentWeatherComponent {

    /**
     * Current Weather Card when data is fetched successfully
     */
    @Composable
    fun CurrentConditionsCardSuccess(value: CurrentWeather) {
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
                            .data("http://openweathermap.org/img/wn/${value?.weather?.get(0)?.icon}@2x.png")
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
                        text = value?.name ?: "",
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }

    /**
     * Current Weather Card when data is being fetched
     */
    @Composable
    fun CurrentConditionsLoading(){
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp)
            )
        }
    }

    /**
     * Current weather card when there is an error
     */
    @Composable
    fun CurrentConditionsCardError(error: Errors){
        Card(
            elevation = 2.dp,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(1f),
            shape = RoundedCornerShape(10.dp),
            backgroundColor = CardBackgroundGrey
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
                    contentDescription = "",
                    modifier = Modifier.size(84.dp).padding(0.dp, 0.dp, 12.dp, 0.dp),
                    colorFilter = ColorFilter.tint(color = Color.Red)
                )
                Text(
                    text = error.message,
                    style = TextStyle(color = Color.White, fontSize = 18.sp)
                )
            }
        }
    }

}
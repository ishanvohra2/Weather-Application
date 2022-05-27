package com.ishanvohra.weatherapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ishanvohra.weatherapp.R
import com.ishanvohra.weatherapp.model.Errors
import com.ishanvohra.weatherapp.model.Forecast
import com.ishanvohra.weatherapp.ui.theme.CardBackgroundGrey
import java.text.SimpleDateFormat
import java.util.*

class ForecastComponent {

    /**
     * Forecast when data is fetched successfully
     */
    @Composable
    fun ForecastListSuccess(foreCastData: Forecast){
        LazyRow(
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            itemsIndexed(foreCastData.list){_: Int, item: Forecast.Item ->
                ForecastItem(item = item)
            }
        }
    }

    /**
     * Forecast list item
     */
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
                        .data("http://openweathermap.org/img/wn/${item.weather[0].icon}@4x.png")
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
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(2.dp, 0.dp, 0.dp, 6.dp),
                        color = Color.Yellow,
                    )
                }
            }
        }
    }

    /**
     * Forecast card when data is being fetched
     */

    @Composable
    fun ForecastLoading(){
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
    fun ForecastError(error: Errors){
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
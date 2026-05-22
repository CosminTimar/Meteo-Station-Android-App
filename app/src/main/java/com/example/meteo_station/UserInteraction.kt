package com.example.meteo_station

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview(name = "da")
fun MainComposer(){
    var temperatureInput by remember {mutableStateOf(0)}
    var preasureInput by remember { mutableStateOf(0) }
    var uvIndexInput by remember { mutableStateOf(0) }
    var co2Input by remember { mutableStateOf(0) }
    var organicCoumpoundInput by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Row(
            modifier = Modifier.padding(bottom = 50.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 30.sp
            )
        }

        EnvironmentElement(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
                .background(Color.Gray),
                "Temperature",
                temperatureInput,
                "\u2103")

        Row(
            Modifier.padding(0.dp,100.dp)
        ) {
            Column(
                Modifier.padding(top = 50.dp, start = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                EnvironmentElement(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
                        .background(Color.Gray),
                    "CO2",
                    co2Input,
                    "ppm")
                EnvironmentElement(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(100.dp))
                        .background(Color.Gray),
                    "UV Index",
                    uvIndexInput,
                    "")

            }


            Column(
                Modifier.padding(top = 50.dp, start = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                EnvironmentElement(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(100.dp))
                        .background(Color.Gray),
                    "Pressure",
                    preasureInput,
                    "hPa")
                EnvironmentElement(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
                        .background(Color.Gray),
                    "TVOH",
                    organicCoumpoundInput,
                    "ppb")
            }
        }

    }
}

@Composable
fun EnvironmentElement(
    modifier: Modifier,
    label: String,
    envData: Number,
    measurementUnit: String
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 10.dp),
            fontSize = 20.sp,
            color = Color.White,
        )
        Text(
            text = "$envData $measurementUnit",
            fontSize = 40.sp,
            color = Color.White,
        )
    }
}
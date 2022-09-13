package com.studymate.application.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.studymate.application.data.MeasurementData
import com.studymate.application.service.ApiService.Companion.apiService
import java.time.LocalDateTime

@Composable
fun Measurement(openDrawer: () -> Unit) {
    val context = LocalContext.current
    var measurementDataList by remember { mutableStateOf(listOf<MeasurementData>()) }

    LaunchedEffect(measurementDataList) {
        try {
            val response = apiService?.retrieveMeasureData()
            if (response != null) {
                measurementDataList = response
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    LazyColumn {
        item {
            MainTopBar(
                title = DrawerScreens.Measurement.title,
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() }
            )
        }
        items(measurementDataList) { measurementData ->
            val localDate = LocalDateTime.parse(measurementData.timestamp).toLocalDate()
            val localTime = LocalDateTime.parse(measurementData.timestamp).toLocalTime()
            Text(
                text = "${localDate.dayOfMonth}일 ${localTime.hour}시 ${localTime.minute}분 ${localTime.second}초 : 온도 ${measurementData.temperature}, 습도 ${measurementData.humidity}",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
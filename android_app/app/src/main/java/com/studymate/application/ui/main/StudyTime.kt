package com.studymate.application.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.studymate.application.service.ApiService
import kotlin.time.Duration

@Composable
fun StudyTime(
    openDrawer: () -> Unit,
    userId: String
) {
    val context = LocalContext.current
    val apiService = ApiService.getInstance(context)
    var currentFocusTime by remember { mutableStateOf("PT0S") }
    var currentNonFocusTime by remember { mutableStateOf("PT0S") }
    var totalFocusTime by remember { mutableStateOf("PT0S") }
    var totalNonFocusTime by remember { mutableStateOf("PT0S") }

    LaunchedEffect(true) {
        try {
            val currentFocus =
                apiService.retrieveStudyTime("current", "focus", userId)
            val currentNonFocus =
                apiService.retrieveStudyTime(
                    "current",
                    "non-focus",
                    userId
                )
            val totalFocus =
                apiService.retrieveStudyTime("total", "focus", userId)
            val totalNonFocus =
                apiService.retrieveStudyTime("total", "non-focus", userId)
            currentFocusTime = currentFocus
            currentNonFocusTime = currentNonFocus
            totalFocusTime = totalFocus
            totalNonFocusTime = totalNonFocus
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        MainTopBar(
            title = DrawerScreens.StudyTime.title,
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "최근 집중 시간", style = MaterialTheme.typography.body1)
            Text(
                text = "${Duration.parse(currentFocusTime.replace("\"", "")).inWholeMinutes}분",
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "최근 총 공부 시간", style = MaterialTheme.typography.body1)
            Text(
                text = "${Duration.parse(currentNonFocusTime.replace("\"", "")).inWholeMinutes}분",
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "누적 집중 시간", style = MaterialTheme.typography.body1)
            Text(
                text = "${Duration.parse(totalFocusTime.replace("\"", "")).inWholeMinutes}분",
                style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "누적 총 공부 시간", style = MaterialTheme.typography.body1)
            Text(
                text = "${Duration.parse(totalNonFocusTime.replace("\"", "")).inWholeMinutes}분",
                style = MaterialTheme.typography.h3
            )
        }
    }
}
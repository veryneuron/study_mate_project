package com.studymate.application.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.studymate.application.model.UserState
import com.studymate.application.model.WebSocketViewModel
import com.studymate.application.service.ApiService
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun Monitoring(openDrawer: () -> Unit, token: String?, userId: String) {
    val context = LocalContext.current
    val apiService = ApiService.getInstance(context)
    val viewModel = remember {WebSocketViewModel(context, token!!)}

    LaunchedEffect(viewModel) {
        var currentTime = Duration.ZERO
        var currentRecord = Duration.ZERO
        try {
            val currentUserStatus = apiService.checkUserStatus(listOf(userId))
            if (currentUserStatus.userStatus[0].isTiming) {
                currentTime =
                    Duration.parse(
                        apiService.retrieveStudyTime("current", "non-focus", userId)
                            .replace("\"", "")
                    )
            }
            if (currentUserStatus.userStatus[0].isRecording) {
                currentRecord =
                    Duration.parse(
                        apiService.retrieveStudyTime("current", "focus", userId).replace("\"", "")
                    )
            }
            viewModel.userStateList.add(
                UserState(
                    userId,
                    currentUserStatus.userStatus[0].isTiming,
                    currentUserStatus.userStatus[0].isRecording,
                    currentTime,
                    currentRecord,
                )
            )
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }
    DisposableEffect(viewModel) {
        onDispose {viewModel.onStop()}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        MainTopBar(
            title = DrawerScreens.Monitoring.title,
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )
        LaunchedEffect(viewModel) {
            while(true) {
                delay(1.seconds)
                for (userState in viewModel.userStateList) {
                    if (userState.isStudying) {
                        userState.studyTime =
                            userState.studyTime.plus(1.seconds)
                    }
                    if (userState.isRecording) {
                        userState.studyRecord =
                            userState.studyRecord.plus(1.seconds)
                    }
                }
            }
        }
        for (userState in viewModel.userStateList) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    if (userState.userId == userId) {
                        Text(text = "나의 공부시간")
                    } else {
                        Text(text = "${userState.userId}님의 공부시간")
                    }
                    Text(text = "현재 공부시간 : ${userState.studyTime.inWholeMinutes}분" +
                            " ${userState.studyTime.inWholeSeconds.rem(60)}초")
                    Text(text = "현재 집중시간 : ${userState.studyRecord.inWholeMinutes}분" +
                            " ${userState.studyRecord.inWholeSeconds.rem(60)}초")
                }
            }
        }
    }
}
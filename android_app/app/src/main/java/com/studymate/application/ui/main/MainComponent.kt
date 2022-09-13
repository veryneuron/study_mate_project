package com.studymate.application.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class DrawerScreens(val title: String, val route: String) {
    object Monitoring : DrawerScreens("공부 모니터링", "Monitoring")
    object Measurement : DrawerScreens("온도 및 습도 확인", "Measurement")
    object UserProfile : DrawerScreens("사용자 정보 수정", "UserProfile")
    object UserMachine : DrawerScreens("사용자 기기 수정", "UserMachine")
    object StudyTime : DrawerScreens("공부 시간 확인", "StudyTime")
}

private val screens = listOf(
    DrawerScreens.Monitoring,
    DrawerScreens.Measurement,
    DrawerScreens.UserProfile,
    DrawerScreens.UserMachine,
    DrawerScreens.StudyTime
)

@Composable
fun MainDrawer(
    modifier: Modifier = Modifier,
    onDestinationClicked: (route: String) -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 48.dp)
    ) {
        screens.forEach { screen ->
            Spacer(Modifier.height(24.dp))
            Text(
                text = screen.title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.clickable {
                    onDestinationClicked(screen.route)
                }
            )
        }
    }
}

@Composable
fun MainTopBar(title: String = "", buttonIcon: ImageVector, onButtonClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            IconButton(onClick = { onButtonClicked() }) {
                Icon(buttonIcon, contentDescription = "")
            }
        },
        backgroundColor = MaterialTheme.colors.primaryVariant
    )
}


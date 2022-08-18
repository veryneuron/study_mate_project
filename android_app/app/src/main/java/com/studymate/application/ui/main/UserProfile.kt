package com.studymate.application.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun UserProfile(
    openDrawer: () -> Unit,
    onClickLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MainTopBar(
            title = "사용자 정보 확인",
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "사용자 정보 화면", style = MaterialTheme.typography.h4)
            Text(
                text = "로그아웃",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.clickable {
                    onClickLogout()
                }
            )
        }
    }
}
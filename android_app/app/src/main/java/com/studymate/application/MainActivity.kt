package com.studymate.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studymate.application.ui.login.LoginScreen
import com.studymate.application.ui.main.MainScreen
import com.studymate.application.ui.theme.ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigateStudy()
                }
            }
        }
    }
}

@Composable
fun NavigateStudy() {
    ApplicationTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "Login",
        ) {
            composable("Login") {
                LoginScreen(onClickLogin = {
                    navController.navigate("Main") {
                        popUpTo(0)
                    }
                })
            }
            composable("Main") {
                MainScreen(onClickLogout = {
                    navController.navigate("Login") {
                        popUpTo(0)
                    }
                })
            }
        }
    }
}
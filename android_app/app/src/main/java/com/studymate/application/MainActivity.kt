package com.studymate.application

import android.content.Context
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
import com.studymate.application.model.AuthViewModel
import com.studymate.application.ui.auth.LoginScreen
import com.studymate.application.ui.auth.SignUpScreen
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
                    NavigateStudy(this)
                }
            }
        }
    }
}

@Composable
fun NavigateStudy(context: Context) {
    ApplicationTheme {
        val navController = rememberNavController()
        val authViewModel = AuthViewModel(context)
        NavHost(
            navController = navController,
            startDestination = "Login",
        ) {
            composable("Login") {
                LoginScreen(
                    onClickLogin = {
                        authViewModel.signIn {
                            navController.navigate("Main") {
                                popUpTo(0)
                            }
                        }
                    },
                    onClickSignUp = { navController.navigate("SignUp") },
                    authInfo = authViewModel.userDataResponse
                )
            }
            composable("Main") {
                MainScreen(
                    onClickLogout = {
                        navController.navigate("Login") {
                            popUpTo(0)
                        }
                    },
                    authViewModel = authViewModel
                )
            }
            composable("SignUp") {
                SignUpScreen(
                    onClickSignUp = {
                        authViewModel.signUp {
                            navController.navigate("Login") {
                                popUpTo(0)
                            }
                        }
                    },
                    authInfo = authViewModel.userDataResponse
                )
            }
        }
    }
}
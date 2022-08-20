package com.studymate.application.ui.main

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studymate.application.model.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onClickLogout: () -> Unit,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    Surface(color = MaterialTheme.colors.background) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val openDrawer = {
            scope.launch {
                drawerState.open()
            }
        }
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                MainDrawer(
                    onDestinationClicked = { route ->
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = DrawerScreens.Monitoring.route
            ) {
                composable(DrawerScreens.Monitoring.route) {
                    Monitoring(
                        openDrawer = {
                            openDrawer()
                        }
                    )
                }
                composable(DrawerScreens.Measurement.route) {
                    Measurement(
                        openDrawer = {
                            openDrawer()
                        }
                    )
                }
                composable(DrawerScreens.UserProfile.route) {
                    UserProfile(
                        openDrawer = {
                            openDrawer()
                        },
                        onClickLogout = onClickLogout,
                        authViewModel = authViewModel
                    )
                }
                composable(DrawerScreens.UserMachine.route) {
                    UserMachine(
                        openDrawer = {
                            openDrawer()
                        }
                    )
                }
                composable(DrawerScreens.StudyTime.route) {
                    StudyTime(
                        openDrawer = {
                            openDrawer()
                        },
                        authViewModel.userDataResponse.userId
                    )
                }
            }
        }
    }
}
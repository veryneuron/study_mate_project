package com.studymate.application.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.application.data.AuthDTO
import com.studymate.application.model.AuthViewModel

@Composable
fun UserProfile(
    openDrawer: () -> Unit,
    onClickLogout: () -> Unit,
    authViewModel: AuthViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MainTopBar(
            title = DrawerScreens.UserProfile.title,
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = authViewModel.userDataResponse.userId + "님 환영합니다!")

            var tempNickname by remember { mutableStateOf(authViewModel.userDataResponse.nickname) }
            var tempPassword by remember { mutableStateOf(authViewModel.userDataResponse.userPassword) }

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "닉네임") },
                value = tempNickname,
                onValueChange = { tempNickname = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "패스워드") },
                value = tempPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { tempPassword = it })

            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        authViewModel.editUserProfile(
                            AuthDTO(
                                authViewModel.userDataResponse.userId,
                                tempNickname,
                                tempPassword
                            )
                        ) {
                            authViewModel.userDataResponse.nickname = tempNickname
                            authViewModel.userDataResponse.userPassword = tempPassword
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "회원정보 수정")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            ClickableText(
                text = AnnotatedString("로그아웃"),
                onClick = {
                    authViewModel.sessionManager.clearAuthToken()
                    onClickLogout()
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            ClickableText(
                text = AnnotatedString("회원탈퇴"),
                onClick = {
                    authViewModel.deleteUserProfile {
                        authViewModel.sessionManager.clearAuthToken()
                        onClickLogout()
                    }},
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default
                )
            )
        }
    }
}
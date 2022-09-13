package com.studymate.application.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.studymate.application.model.AuthState
import com.studymate.application.ui.theme.ApplicationTheme

@Composable
fun SignUpScreen(
    onClickSignUp: () -> Unit,
    authInfo: AuthState
) {
    ApplicationTheme {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "아이디") },
                    value = authInfo.userId,
                    onValueChange = { authInfo.userId = it })

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "닉네임") },
                    value = authInfo.nickname,
                    onValueChange = { authInfo.nickname = it })

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "패스워드") },
                    value = authInfo.userPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { authInfo.userPassword = it })

                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = { onClickSignUp() },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = " 회원 가입")
                    }
                }
            }
        }
    }
}
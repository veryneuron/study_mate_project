package com.studymate.application.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.studymate.application.data.RegistrationDTO
import com.studymate.application.service.ApiService
import kotlinx.coroutines.launch

@Composable
fun UserMachine(openDrawer: () -> Unit) {
    val context = LocalContext.current
    val machineSetting = remember {RegistrationState()}
    val apiService = ApiService.getInstance(context)
    val scope = rememberCoroutineScope()

    LaunchedEffect(machineSetting) {
        try {
            val response = apiService.getSettingValue()
            machineSetting.humiditySetting = response.humiditySetting.toString()
            machineSetting.temperatureSetting = response.temperatureSetting.toString()
            machineSetting.rasberrypiAddress = response.rasberrypiAddress ?: ""
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        MainTopBar(
            title = DrawerScreens.UserMachine.title,
            buttonIcon = Icons.Filled.Menu,
            onButtonClicked = { openDrawer() }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "라즈베리파이 설정")

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "온도") },
                value = machineSetting.temperatureSetting,
                onValueChange = { machineSetting.temperatureSetting = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "습도") },
                value = machineSetting.humiditySetting,
                onValueChange = { machineSetting.humiditySetting = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "주소") },
                value = machineSetting.rasberrypiAddress,
                onValueChange = { machineSetting.rasberrypiAddress = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val result = ApiService.getInstance(context).setSettingValue(
                                    RegistrationDTO(
                                        humiditySetting = machineSetting.humiditySetting.toFloat(),
                                        temperatureSetting = machineSetting.temperatureSetting.toFloat(),
                                        rasberrypiAddress = machineSetting.rasberrypiAddress
                                    )
                                )
                                if (result != "Successfully set value") throw Exception(result)
                                Toast.makeText(context, "수정 성공!", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "라즈베리파이 정보 수정")
                }
            }
        }
    }
}

class RegistrationState(
    _temperatureSetting: String = "",
    _humiditySetting: String = "",
    _rasberrypiAddress: String = ""
) {
    var temperatureSetting: String by mutableStateOf("")
    var humiditySetting: String by mutableStateOf("")
    var rasberrypiAddress: String by mutableStateOf("")

    init {
        temperatureSetting = _temperatureSetting
        humiditySetting = _humiditySetting
        rasberrypiAddress = _rasberrypiAddress
    }
}
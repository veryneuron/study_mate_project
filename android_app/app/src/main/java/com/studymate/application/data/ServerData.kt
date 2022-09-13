package com.studymate.application.data


data class AuthDTO(
    var userId : String,
    var nickname : String,
    var userPassword : String,
)

data class RegistrationDTO(
    var temperatureSetting : Float,
    var humiditySetting : Float,
    var rasberrypiAddress : String?,
)

data class UserStatusDTO(
    var userStatus : List<UserStatus>
)

data class UserStatus(
    var userId : String,
    var isTiming : Boolean,
    var isRecording : Boolean,
)

data class MeasurementData(
    var temperature : String,
    var humidity : String,
    var timestamp: String,
    var raspberrypiAddress: String,
    var userId: String,
)

data class ConnData(
    var userId: String,
    var type : String,
)
package com.studymate.application.data

import java.time.LocalDateTime


data class AuthDTO(
    var userId : String,
    var nickname : String,
    var userPassword : String,
)

data class RegistrationDTO(
    var temperatureSetting : Float,
    var humiditySetting : Float,
    var rasberrypiAddress : String,
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
    var temperature : Float,
    var humidity : Float,
    var timestamp: LocalDateTime,
    var rasberrypiAddress: String,
    var userId: String,
)
package com.studymate.application.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.studymate.application.data.ConnData
import com.studymate.application.service.ApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.lang.ref.WeakReference
import kotlin.time.Duration

class UserState(
    _userId: String,
    _isStudying: Boolean,
    _isRecording: Boolean,
    _studyTime: Duration,
    _studyRecord: Duration
) {
    var userId by mutableStateOf("")
    var isStudying by mutableStateOf(false)
    var isRecording by mutableStateOf(false)
    var studyTime by mutableStateOf(Duration.ZERO)
    var studyRecord by mutableStateOf(Duration.ZERO)

    init {
        userId = _userId
        isStudying = _isStudying
        isRecording = _isRecording
        studyTime = _studyTime
        studyRecord = _studyRecord
    }
}

class WebSocketViewModel(context: Context, token: String) : ViewModel() {
    private var ws: WebSocket? = null
    private val client by lazy { OkHttpClient() }
    var userStateList = mutableStateListOf<UserState>()
    private val currentContext : WeakReference<Context> = WeakReference(context)
    private val gson = Gson()
    private val apiService = ApiService.getInstance(context)

    init {
        val request = Request.Builder().url("ws://192.168.56.1:8080/{$token}").build()
        val listener = object: WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    connDataProcessing(gson.fromJson(text, ConnData::class.java))
                } catch (e: Exception) {
                    Toast.makeText(currentContext.get(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        ws = client.newWebSocket(request, listener)
    }

    fun onStop() {
        ws?.close(1000, "")
    }

    fun connDataProcessing(data: ConnData) {
        when (data.type) {
            "Connected" -> {
                viewModelScope.launch {
                    val status = apiService.checkUserStatus(listOf(data.userId)).userStatus
                    val studyTime =
                        apiService.retrieveStudyTime("current", "non-focus", data.userId).replace("\"", "")
                    val studyRecord = apiService.retrieveStudyTime("current", "focus", data.userId).replace("\"", "")
                    userStateList.add(
                        UserState(
                            data.userId,
                            status[0].isTiming,
                            status[0].isRecording,
                            Duration.parse(studyTime),
                            Duration.parse(studyRecord)
                        )
                    )
                }
            }
            "Disconnected" -> {
                userStateList.remove(userStateList.find { it.userId == data.userId })
            }
            "Unauthorized" -> {
                throw Exception("Unauthorized")
            }
            "StudyTime" -> {
                userStateList.find { it.userId == data.userId }?.isStudying = !userStateList.find { it.userId == data.userId }?.isStudying!!
            }
            "StudyRecord" -> {
                userStateList.find { it.userId == data.userId }?.isRecording = !userStateList.find { it.userId == data.userId }?.isRecording!!
            }
        }

    }
}


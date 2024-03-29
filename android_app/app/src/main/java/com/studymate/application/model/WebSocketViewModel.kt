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
import com.studymate.application.service.AuthInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.lang.ref.WeakReference
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
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
    private val client = okhttpClient(context)
    var userStateList = mutableStateListOf<UserState>()
    private val currentContext : WeakReference<Context> = WeakReference(context)
    private val gson = Gson()
    private val apiService = ApiService.getInstance(context)

    init {
        val request = Request.Builder().url("wss:/ec2-18-183-112-167.ap-northeast-1.compute.amazonaws.com/websocket/{$token}").build()
//        val request = Request.Builder().url("wss://192.168.50.17/websocket/{$token}").build()
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

    private fun okhttpClient(context: Context): OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient().newBuilder()
            .addInterceptor(AuthInterceptor(context))
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun onStop() {
        ws?.close(1000, "")
    }

    fun connDataProcessing(data: ConnData) {
        when (data.type) {
            "Connected" -> {
                viewModelScope.launch {
                    var studyTime = Duration.ZERO
                    var studyRecord = Duration.ZERO
                    val status = apiService.checkUserStatus(listOf(data.userId)).userStatus
                    if (status[0].isTiming) {
                        studyTime =
                            Duration.parse(
                                apiService.retrieveStudyTime(
                                    "current",
                                    "non-focus",
                                    data.userId
                                ).replace("\"", "")
                            )
                    }
                    if (status[0].isRecording) {
                        studyRecord =
                            Duration.parse(
                                apiService.retrieveStudyTime(
                                    "current",
                                    "focus",
                                    data.userId
                                ).replace("\"", "")
                            )
                    }
                    userStateList.add(
                        UserState(
                            data.userId,
                            status[0].isTiming,
                            status[0].isRecording,
                            studyTime,
                            studyRecord,
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
                val nowStudying = userStateList.find { it.userId == data.userId }?.isStudying
                val nowRecording = userStateList.find { it.userId == data.userId }?.isRecording
                if (nowStudying == true && nowRecording == true) {
                    userStateList.find { it.userId == data.userId }?.isRecording = false
                }
                if (nowStudying == false && nowRecording == false) {
                    userStateList.find { it.userId == data.userId }?.isRecording = true
                }
                userStateList.find { it.userId == data.userId }?.isStudying = !nowStudying!!
            }
            "StudyRecord" -> {
                userStateList.find { it.userId == data.userId }?.isRecording =
                    !userStateList.find { it.userId == data.userId }?.isRecording!!
            }
        }

    }
}


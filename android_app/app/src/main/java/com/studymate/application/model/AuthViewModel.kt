package com.studymate.application.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.application.data.AuthDTO
import com.studymate.application.service.ApiService
import com.studymate.application.ui.auth.SessionManager
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class AuthState(
    _userId: String = "",
    _nickname: String = "",
    _userPassword: String = "",
) {
    var userId by mutableStateOf("")
    var nickname by mutableStateOf("")
    var userPassword by mutableStateOf("")

    init {
        userId = _userId
        nickname = _nickname
        userPassword = _userPassword
    }
}

private fun stateConverter(state : AuthState) : AuthDTO {
    return AuthDTO(
        userId = state.userId,
        nickname = state.nickname,
        userPassword = state.userPassword
    )
}

private fun stateConverter(dto : AuthDTO) : AuthState {
    return AuthState(
        _userId = dto.userId,
        _nickname = dto.nickname,
        _userPassword = dto.userPassword
    )
}

class AuthViewModel(context: Context) : ViewModel() {
    val sessionManager = SessionManager(WeakReference(context).get()!!)
    private val apiService = ApiService.getInstance(context)
    var userDataResponse = AuthState()
    private val currentContext : WeakReference<Context> = WeakReference(context)

    fun signIn(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val result = apiService.signin(stateConverter(userDataResponse))
                if (result.isNotEmpty()) {
                    sessionManager.saveAuthToken(result)
                    val userData = apiService.getUserData()
                    userDataResponse.userId = userData.userId
                    userDataResponse.nickname = userData.nickname
                    onSuccess()
                } else {
                    throw Exception("로그인 실패!")
                }
            } catch (e: Exception) {
                Toast.makeText(currentContext.get(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    fun signUp(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val result = apiService.signup(stateConverter(userDataResponse))
                if (result == "Successfully signed up") {
                    onSuccess()
                } else {
                    throw Exception(result)
                }
            } catch (e: Exception) {
                Toast.makeText(currentContext.get(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    fun editUserProfile(userData: AuthDTO, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val result = apiService.editing(userData)
                if (result == "Successfully edited") {
                    onSuccess()
                } else {
                    throw Exception(result)
                }
            } catch (e: Exception) {
                Toast.makeText(currentContext.get(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    fun deleteUserProfile(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val result = apiService.deleting()
                if (result == "Successfully deleted") {
                    onSuccess()
                } else {
                    throw Exception(result)
                }
            } catch (e: Exception) {
                Toast.makeText(currentContext.get(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
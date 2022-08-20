package com.studymate.application.service

import android.content.Context
import com.google.gson.GsonBuilder
import com.studymate.application.data.AuthDTO
import com.studymate.application.data.MeasurementData
import com.studymate.application.data.RegistrationDTO
import com.studymate.application.data.UserStatusDTO
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/signup")
    suspend fun signup(@Body authDTO: AuthDTO): String

    @POST("/api/auth/signin")
    suspend fun signin(@Body authDTO: AuthDTO): String

    @GET("/api/auth")
    suspend fun getUserData(): AuthDTO

    @PUT("/api/auth")
    suspend fun editing(@Body authDTO: AuthDTO): String

    @DELETE("/api/auth")
    suspend fun deleting(): String

    @GET("/api/registration")
    suspend fun getSettingValue(): RegistrationDTO

    @PUT("/api/registration")
    suspend fun setSettingValue(@Body registrationDTO: RegistrationDTO): String

    /* current,total / focus,non-focus */
    @GET("/api/study/{time}/{focus}")
    suspend fun retrieveStudyTime(@Path("time") time: String
                          ,@Path("focus") focus: String
                          ,@Query("userId") userId : String): String

    @GET("/api/study")
    suspend fun checkUserStatus(@Query("userIds") userIds: List<String>): UserStatusDTO

    @GET("/api/measurement")
    suspend fun retrieveMeasureData(): List<MeasurementData>

    companion object {
        var apiService: ApiService? = null
        fun getInstance(context: Context): ApiService {
            val gson = GsonBuilder().create()
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("http://192.168.56.1:8000")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okhttpClient(context))
                    .build()
                    .create(ApiService::class.java)
            }
            return apiService!!
        }

        private fun okhttpClient(context: Context): OkHttpClient {
            return OkHttpClient().newBuilder()
                .addInterceptor(AuthInterceptor(context))
                .build()
        }
    }
}
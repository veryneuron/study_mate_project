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
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

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
                    .baseUrl("https://ec2-13-231-168-213.ap-northeast-1.compute.amazonaws.com/api/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okhttpClient(context))
                    .build()
                    .create(ApiService::class.java)
            }
            return apiService!!
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
    }
}
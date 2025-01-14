package kh.edu.rupp.ite.viewmodelv2.service

import AuthInterceptor
import kh.edu.rupp.ite.viewmodelv3.globle.ApiConstance.Companion.BASE_API
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {

    private val logInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val _http = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(logInterceptor)
        .build()

    private var apiService: ApiService? = null

    fun getApiService(): ApiService {
        if (apiService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_API)
                .client(_http)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}

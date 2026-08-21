package com.gemma.tensinini.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Prepara la conexión con el backend, una sola vez para toda la app.

object RetrofitClient {

    private const val BASE_URL="http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            //Traduce JSON <-> Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
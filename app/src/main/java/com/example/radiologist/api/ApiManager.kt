package com.example.radiologist.api

import android.util.Log
import com.example.radiologist.model.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {
    private var retrofit: Retrofit
    init {
        val logging = HttpLoggingInterceptor { message -> Log.e("api->", message) }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        retrofit= Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val modelServices: ModelServices by lazy {
        retrofit.create(ModelServices::class.java)
    }

}
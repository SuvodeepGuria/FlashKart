package com.example.flashkart.network

import com.example.flashkart.BuildConfig
import com.example.flashkart.Data.InternetItem
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = BuildConfig.BASE_URL  //The Base Url is hide because if it will be public then any one can access my Json file link.
private val json = Json { ignoreUnknownKeys = true }
@OptIn(ExperimentalSerializationApi::class)
private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        json.asConverterFactory(
            "application/json".toMediaType()
        )
    )
    .baseUrl(BASE_URL)
    .build()

interface FlashApiService {
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItems(): List<InternetItem>
}

object FlashApi {
    val retrofitService: FlashApiService by lazy {
        retrofit.create(FlashApiService::class.java)
    }
}

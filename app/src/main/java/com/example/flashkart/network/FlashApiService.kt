package com.example.flashkart.network

import com.example.flashkart.Data.InternetItem
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://training-uploads.internshala.com"
private val json = Json { ignoreUnknownKeys = true }
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

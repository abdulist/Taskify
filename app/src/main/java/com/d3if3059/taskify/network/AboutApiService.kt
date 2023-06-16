package com.d3if3059.taskify.network

import com.d3if3059.taskify.data.About
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

    private const val BASE_URL = "https://raw.githubusercontent.com/" +
            "abdulist/jsonFiles/static-api/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build()
    interface AboutApiService {
        @GET("static-api.json")
        suspend fun getAbout(): List<About>
    }
    object AboutApi {
        val service: AboutApiService by lazy {
            retrofit.create(AboutApiService::class.java)
        }
        fun getAboutUrl(imageId: String): String {
            return "$BASE_URL$imageId.png"
        }
    }
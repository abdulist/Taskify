package com.d3if3059.taskify.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

    private const val BASE_URL = "https://raw.githubusercontent.com/" +
            "abdulist/jsonFiles/static-api/"
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    interface AboutApiService {
        @GET("static-api.json")
        suspend fun getAbout(): String
    }
    object AboutApi {
        val service: AboutApiService by lazy {
            retrofit.create(AboutApiService::class.java)
        }
    }
package com.shahad.dontsayit.data.network

import com.shahad.dontsayit.BuildConfig.mockAPIKey
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MockBuilder {

    private const val BASE_URL =
        "https://$mockAPIKey.mockapi.io/"
    private fun retrofit(): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    val mockAPI: MockAPI =
        retrofit().create(MockAPI::class.java)

}
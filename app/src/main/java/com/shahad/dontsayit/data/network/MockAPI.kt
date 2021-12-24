package com.shahad.dontsayit.data.network

import com.shahad.dontsayit.data.model.Word
import retrofit2.http.GET

interface MockAPI {
    @GET("word")
    suspend fun getWords(): List<Word>


    /*@POST("users")
    suspend fun addUser(@Body user: User): User
*/
}
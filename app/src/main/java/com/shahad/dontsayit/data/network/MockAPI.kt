package com.shahad.dontsayit.data.network

import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.model.Word
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MockAPI {
    @GET("word")
    suspend fun getWords(): List<Word>

    @GET("profilePic")
    suspend fun getProfilePictures(): List<ProfilePicture>

    @POST("userRequests")
    suspend fun userRequests(@Body gameValue: UserSuggestions)

    @GET("profilePic/:id")
    suspend fun getUserProfilePic(@Query("query") id: String): ProfilePicture


    /*@POST("users")
    suspend fun addUser(@Body user: User): User
*/
}
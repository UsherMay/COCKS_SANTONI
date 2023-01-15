package com.yvonbaptiste.todo.user

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserWebService {
    @GET("/sync/v9/user/")
    suspend fun fetchUser(): Response<User>

    @Multipart
    @POST("sync/v9/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<User>

    @PATCH("sync/v9/user")
    suspend fun update(@Body user: UserUpdate): Response<Unit>
}
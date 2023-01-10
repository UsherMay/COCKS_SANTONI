package com.yvonbaptiste.todo.data

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserWebService {
    @GET("/sync/v9/user/")
    suspend fun fetchUser(): Response<User>
}
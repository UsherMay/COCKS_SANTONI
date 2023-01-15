package com.yvonbaptiste.todo.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("email")
    var email: String,
    @SerialName("full_name")
    var name: String,
    @SerialName("avatar_medium")
    val avatar: String? = null
)

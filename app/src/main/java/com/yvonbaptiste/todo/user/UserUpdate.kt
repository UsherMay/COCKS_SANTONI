//ADDED
package com.yvonbaptiste.todo.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserUpdate(
    @SerialName("full_name")
    val name: String? = null,
    @SerialName("email")
    val email: String? = null,
)
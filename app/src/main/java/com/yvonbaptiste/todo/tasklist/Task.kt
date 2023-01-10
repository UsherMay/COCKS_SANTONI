package com.yvonbaptiste.todo.tasklist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    @SerialName("content")
    val title: String,
    @SerialName("description")
    val description: String = "",
    @SerialName("id")
    val id: String) : java.io.Serializable

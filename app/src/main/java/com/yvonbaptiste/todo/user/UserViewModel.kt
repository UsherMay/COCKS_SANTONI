package com.yvonbaptiste.todo.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yvonbaptiste.todo.data.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val webService = Api.userWebService

    val userStateFlow = MutableStateFlow<User>(User(name = "", email = ""))

    fun refresh() {
        viewModelScope.launch {
            val response = webService.fetchUser()
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            val fetchedUser = response.body()!!
            userStateFlow.value = fetchedUser
        }
    }

    fun edit(user: User) {
        viewModelScope.launch {
            val userUpdate = UserUpdate(name = user.name, email = user.email)
            val response = webService.update(userUpdate)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }

            // On a essayé plusieurs choses pour les lignes suivantes, mais rien ne marchait
            // La difficulté principale c'est que la réponse est Unit, du coup on ne savait
            // pas trop comment s'y prendre (userstateFlow.value attend un User)
            val updatedUser = webService.fetchUser() as User
            userStateFlow.value = User(name = updatedUser.name, email = updatedUser.email)
        }
    }
}
package com.flexath.themoviebookingapp.network.auth

import com.flexath.themoviebookingapp.data.vos.signin.UserVO

interface AuthManager {
    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit)
    fun register(userName: String,
                 email: String,
                 password: String,
                 phoneNumber: String,
                 onSuccess: (user: UserVO) -> Unit,
                 onFailure: (String) -> Unit)
    fun forgetPassword(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit)

    fun getUserId() : String
}
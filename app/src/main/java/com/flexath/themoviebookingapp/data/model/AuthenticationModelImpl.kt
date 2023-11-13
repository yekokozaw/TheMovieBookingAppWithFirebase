package com.flexath.themoviebookingapp.data.model

import com.flexath.themoviebookingapp.data.vos.signin.UserVO
import com.flexath.themoviebookingapp.network.auth.AuthManager
import com.flexath.themoviebookingapp.network.auth.FirebaseAuthManager

object AuthenticationModelImpl : AuthenticationModel {

    override var mAuthManager: AuthManager = FirebaseAuthManager

    override fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        mAuthManager.login(email, password, onSuccess, onFailure)
    }

    override fun register(
        userName: String,
        email: String,
        password: String,
        phoneNumber: String,
        onSuccess: (user: UserVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mAuthManager.register(userName,email, password,phoneNumber,onSuccess, onFailure)
    }

    override fun forgetPassword(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        mAuthManager.forgetPassword(email, onSuccess, onFailure)
    }

    override fun getUserId(): String {
        return mAuthManager.getUserId()
    }

}
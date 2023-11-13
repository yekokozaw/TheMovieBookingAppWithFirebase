package com.flexath.themoviebookingapp.network.firebase

import com.flexath.themoviebookingapp.data.vos.signin.UserVO

interface CloudFirestoreFirebaseApi {

    fun addUser(user: UserVO)

    fun getUsers(
        onSuccess: (users: List<UserVO>) -> Unit, onFailure: (String) -> Unit
    )



}
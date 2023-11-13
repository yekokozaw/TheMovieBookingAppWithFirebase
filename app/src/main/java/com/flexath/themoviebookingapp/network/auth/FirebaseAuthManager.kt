package com.flexath.themoviebookingapp.network.auth

import com.flexath.themoviebookingapp.data.vos.signin.UserVO
import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthManager : AuthManager {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful && it.isComplete){
                onSuccess()
            } else {
                onFailure(it.exception?.message ?: "Please Check Internet Connection")
            }
        }
    }

    override fun register(
        userName: String,
        email: String,
        password: String,
        phoneNumber: String,
        onSuccess: (user : UserVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful && it.isComplete) {

                onSuccess(UserVO(getUserId(), userName,email,password,phoneNumber))

            } else {
                onFailure(it.exception?.message ?: "Please check internet connection")
            }
        }
    }

    override fun forgetPassword(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        mFirebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Please check internet connection")
                }
            }
    }

    override fun getUserId(): String {
        return mFirebaseAuth.currentUser?.uid.toString()
    }

}
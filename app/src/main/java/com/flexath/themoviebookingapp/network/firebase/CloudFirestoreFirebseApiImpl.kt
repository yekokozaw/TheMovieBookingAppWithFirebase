package com.flexath.themoviebookingapp.network.firebase

import android.util.Log
import com.flexath.themoviebookingapp.data.vos.signin.UserVO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object CloudFirestoreFirebseApiImpl : CloudFirestoreFirebaseApi {

    private var database: FirebaseFirestore = Firebase.firestore

    override fun addUser(user: UserVO) {
        val userMap = hashMapOf(
            "id" to user.userId,
            "name" to user.userName,
            "phone_number" to user.phoneNumber,
            "email" to user.email,
            "password" to user.password
        )
        database.collection("users")
            .document(user.userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.i("FirebaseCall", "Successfully Added")
            }.addOnFailureListener {
                Log.i("FirebaseCall", "Failed Added")
            }

    }

    override fun getUsers(onSuccess: (users: List<UserVO>) -> Unit, onFailure: (String) -> Unit) {
        database.collection("users")
            .addSnapshotListener { value, error ->
                error?.let {
                    onFailure(it.localizedMessage ?: "Check Internet Connection")
                } ?: run {
                    val userList: MutableList<UserVO> = arrayListOf()
                    val documentList = value?.documents ?: arrayListOf()
                    for (document in documentList) {
                        val data = document.data
                        val id = data?.get("id") as String
                        val name = data["name"] as String
                        val phoneNumber = data["phone_number"] as String
                        val email = data["email"] as String
                        val password = data["password"] as String
                        val user = UserVO(
                            id,
                            name,
                            email,
                            password,
                            phoneNumber
                        )
                        userList.add(user)
                    }
                    onSuccess(userList)
                }
            }
    }
}
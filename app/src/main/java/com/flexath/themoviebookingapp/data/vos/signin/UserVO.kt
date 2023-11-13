package com.flexath.themoviebookingapp.data.vos.signin

data class UserVO(
    val userId:String = "",
    var userName : String = "",
    var email : String = "",
    var password : String = "",
    val phoneNumber : String = ""
)
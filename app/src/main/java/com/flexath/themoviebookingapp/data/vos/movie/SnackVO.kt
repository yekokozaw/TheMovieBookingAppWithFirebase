package com.flexath.themoviebookingapp.data.vos.movie

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class SnackVO(

    val categoryId: Int?=0,

    val description: String?="description",

    val id: Int?=0,

    val image: String? = "",

    val name: String?= "",

    val price: Int? =0,

    var quantity: Int = 0
)
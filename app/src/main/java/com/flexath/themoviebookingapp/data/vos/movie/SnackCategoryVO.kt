package com.flexath.themoviebookingapp.data.vos.movie

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class SnackCategoryVO(

    val createdAt: String? = "",

    val id: Int? = 0,

    val isActive: Int? = 0,

    val title: String? = "",

    val titleMm: String? = "",

    val updatedAt: String= ""
)
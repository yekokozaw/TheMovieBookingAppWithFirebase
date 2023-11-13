package com.flexath.themoviebookingapp.data.vos.movie

import com.google.gson.annotations.SerializedName

data class GenreVO (
    @SerializedName("id")
    val id : Int?,

    @SerializedName("name")
    val name : String?
)
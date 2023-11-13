package com.flexath.themoviebookingapp.data.vos.movie

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class PaymentVO(

    var createdAt: String? ="",

    var deletedAt: Any? = Any(),

    var icon: String? = "null",

    var id: Int? = 0,

    var title: String? = "",

    var updatedAt: String? = ""
)
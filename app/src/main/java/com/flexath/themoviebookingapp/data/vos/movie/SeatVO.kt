package com.flexath.themoviebookingapp.data.vos.movie

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SeatVO(

    var id: Int? = 0,

    var type: String? = "",

    var seat_name: String? = "",

    var symbol: String? = "",

    val price: Int? = 0,

    var isSelected:Boolean = false
)
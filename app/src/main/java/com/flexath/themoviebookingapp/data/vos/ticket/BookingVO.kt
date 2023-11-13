package com.flexath.themoviebookingapp.data.vos.ticket

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BookingVO(
    var bookingCode : Int? = 0,

    var isBought : Boolean? = true ,

    var movieName : String? = "",

    var tickets : String? = "",

    var cinema : String? = "",

    var bookingDate : String? = "",
)

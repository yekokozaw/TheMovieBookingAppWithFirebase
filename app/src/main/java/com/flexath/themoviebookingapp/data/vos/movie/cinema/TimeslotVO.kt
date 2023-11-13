package com.flexath.themoviebookingapp.data.vos.movie.cinema

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class TimeslotVO(

    var cinemaDayTimeslotId: Int? = 0,

    var start_time: String? = "",

    var start_date: String? = "",

    var status: Int? = 0
)
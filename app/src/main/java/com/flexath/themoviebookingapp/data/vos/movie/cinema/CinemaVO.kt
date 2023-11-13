package com.flexath.themoviebookingapp.data.vos.movie.cinema

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class CinemaVO(

    var cinema: String? = "",

    var cinemaId: Int? = 0,

    var timeslots: List<TimeslotVO>? = emptyList<TimeslotVO>(),

    var isClicked:Boolean = false
)
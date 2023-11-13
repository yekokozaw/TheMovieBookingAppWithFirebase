package com.flexath.themoviebookingapp.data.vos.movie

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CinemaDetailsVO(

    var address: String? = "",

    val email: String? = "",

    var image : String? = "",

    val facilities: List<FacilityVO>? = listOf(),

    val name: String? = "",

    val phone: String? = "",

    val promoVdoUrl: String? = "",

    val safety: List<String>? = listOf()
)

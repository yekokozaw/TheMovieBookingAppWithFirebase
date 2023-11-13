package com.flexath.themoviebookingapp.data.vos.movie

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BannersVO(
    var title: String? = "",

    var url : String? = "",

    var updated_at : String? = ""
)
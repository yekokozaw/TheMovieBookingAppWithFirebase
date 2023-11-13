package com.flexath.themoviebookingapp.data.vos.movie

import androidx.room.TypeConverters
import com.flexath.themoviebookingapp.persistence.typeconverters.MovieCastsTypeConverter
import com.google.gson.annotations.SerializedName

@TypeConverters(
    MovieCastsTypeConverter::class
)

data class CastsVO(
    @SerializedName("cast")
    val cast : List<CastVO>,

    @SerializedName("crew")
    val crew : List<CastVO>
)
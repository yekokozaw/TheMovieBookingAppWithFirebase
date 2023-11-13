package com.flexath.themoviebookingapp.persistence.typeconverters

import androidx.room.TypeConverter
import com.flexath.themoviebookingapp.data.vos.movie.CastVO
import com.flexath.themoviebookingapp.data.vos.movie.CastsVO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CastsVOTypeConverter {
    @TypeConverter
    fun toString(casts: CastsVO?) :String {
        return Gson().toJson(casts)
    }

    @TypeConverter
    fun toCastsVO(jsonString:String) : CastsVO? {
        val movieCastsType = object : TypeToken<CastsVO?>(){}.type
        return Gson().fromJson(jsonString,movieCastsType)
    }
}
package com.flexath.themoviebookingapp.persistence.typeconverters

import androidx.room.TypeConverter
import com.flexath.themoviebookingapp.data.vos.movie.GenreVO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenreTypeConverter {

    @TypeConverter
    fun toString(genre: List<GenreVO>?) :String {
        return Gson().toJson(genre)
    }

    @TypeConverter
    fun toGenreVO(jsonString:String) : List<GenreVO>? {
        val genreVOType = object : TypeToken<List<GenreVO>?>(){}.type
        return Gson().fromJson(jsonString,genreVOType)
    }
}
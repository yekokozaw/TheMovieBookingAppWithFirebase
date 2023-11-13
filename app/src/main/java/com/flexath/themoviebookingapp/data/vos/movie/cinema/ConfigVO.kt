package com.flexath.themoviebookingapp.data.vos.movie.cinema

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.flexath.themoviebookingapp.persistence.typeconverters.TimeslotColorTypeConverter
import com.flexath.themoviebookingapp.persistence.typeconverters.ValueAnyTypeConverter
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@Entity("config_table")
@TypeConverters(
    ValueAnyTypeConverter::class
)
@IgnoreExtraProperties
data class ConfigVO(

    @PrimaryKey
    var id: Int? = 0,

    @ColumnInfo("key")
    var key: String? = "",

    @ColumnInfo("value")
    var value: Any? = Any()
)
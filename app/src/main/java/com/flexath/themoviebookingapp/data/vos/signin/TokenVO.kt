package com.flexath.themoviebookingapp.data.vos.signin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("token_table")
data class TokenVO(
    @ColumnInfo("token")
    val token : String?,

    @PrimaryKey
    val id: Int? = 1
)

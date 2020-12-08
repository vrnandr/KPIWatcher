package com.example.vrnandr.kpiwatcher.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kpi_table")
data class Kpi (
        @PrimaryKey val timestamp : Long,
        @ColumnInfo (name = "personnel_number") val perNum: String,
        val kpi :String
)

@Entity(tableName = "user_table")
data class User(
        @PrimaryKey @ColumnInfo (name = "personnel_number") val perNum : Int,
        @ColumnInfo (name = "full_name") val fullName :String
)
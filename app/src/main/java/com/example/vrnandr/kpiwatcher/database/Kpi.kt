package com.example.vrnandr.kpiwatcher.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kpi")
class Kpi (
    @PrimaryKey
    val timestamp : Long,
    val kpi :String
)

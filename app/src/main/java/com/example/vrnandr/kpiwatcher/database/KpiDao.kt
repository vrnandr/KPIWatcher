package com.example.vrnandr.kpiwatcher.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KpiDao {
    @Insert
    suspend fun insert(kpi: Kpi)

    /* @Query ("select * from kpe_history order by timestamp asc")
     suspend fun getAllValues(): List<Values>*/

    @Query ("select * from kpi order by timestamp desc limit 1")
    fun getCurrentKPE(): LiveData<Kpi>
}
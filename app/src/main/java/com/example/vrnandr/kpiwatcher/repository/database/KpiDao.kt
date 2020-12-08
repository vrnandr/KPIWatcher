package com.example.vrnandr.kpiwatcher.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface KpiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addKPI(kpi: Kpi)

    /* @Query ("select * from kpe_history order by timestamp asc")
     suspend fun getAllValues(): List<Values>*/

    @Query ("select * from kpi_table order by timestamp desc limit 1")
    fun getCurrentKPE(): LiveData<Kpi>

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser (user:User)*/

    /*@Query("SELECT full_name FROM user_table WHERE personnel_number = :perNum ")
    fun getFullName(perNum:Int):String*/
}
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

    @Query ("select * from kpi_table order by timestamp asc")
    fun getAllKPI(): LiveData<List<Kpi>>

    @Query ("select * from kpi_table order by timestamp desc limit 1")
    fun getCurrentKPI(): LiveData<Kpi>

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser (user:User)*/

    /*@Query("SELECT full_name FROM user_table WHERE personnel_number = :perNum ")
    fun getFullName(perNum:Int):String*/
}
package com.example.vrnandr.kpiwatcher.repository

import android.app.Application
import android.content.Context
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.repository.database.KpiDatabase
import com.example.vrnandr.kpiwatcher.repository.network.Api

class Repository private constructor(context: Context) {
    companion object{
        private var INSTANCE: Repository? = null

        fun initialize (context: Context){
            if (INSTANCE == null)
                INSTANCE = Repository(context)
        }

        fun get():Repository{
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }

    private val dao by lazy { KpiDatabase.getInstance(context.applicationContext).kpiDao  }
    private val networkApi by lazy { Api(context as Application) }


    val currentKPI = dao.getCurrentKPI()
    val allKpi = dao.getAllKPI()
    suspend fun addKpi(kpi:Kpi) = dao.addKPI(kpi)

    fun clearCookies() = networkApi.clearCookies()

    val api = networkApi
}
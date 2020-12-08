package com.example.vrnandr.kpiwatcher.repository

import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.repository.database.KpiDao
import com.example.vrnandr.kpiwatcher.repository.network.Api

class Repository(private val dao:KpiDao, val networkApi:Api) {
    val currentKPI = dao.getCurrentKPE()

    suspend fun addKpi(kpi:Kpi){
        dao.addKPI(kpi)
    }

    fun clearCookies(){
        networkApi.clearCookies()
    }

    val api = networkApi
}
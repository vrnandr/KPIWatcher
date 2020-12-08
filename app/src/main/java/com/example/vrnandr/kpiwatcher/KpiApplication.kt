package com.example.vrnandr.kpiwatcher

import android.app.Application
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.KpiDatabase
import com.example.vrnandr.kpiwatcher.repository.network.Api

class KpiApplication : Application() {
    private val databaseDao by lazy { KpiDatabase.getInstance(this).kpiDao }
    private val network by lazy { Api(this) }
    val repositiry by lazy { Repository(databaseDao,network) }
}
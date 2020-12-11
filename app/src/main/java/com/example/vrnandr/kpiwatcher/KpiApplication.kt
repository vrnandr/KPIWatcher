package com.example.vrnandr.kpiwatcher

import android.app.Application
import com.example.vrnandr.kpiwatcher.repository.Repository

class KpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
    }
}
package com.example.vrnandr.kpiwatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CHANNEL_KPI_CHANGE = "kpi_change"
const val NOTIFICATION_CHANNEL_WORKER = "kpi_change"
const val WORKER_TAG = "updateKPI"

class KpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelKPIChanged = NotificationChannel(NOTIFICATION_CHANNEL_KPI_CHANGE, getString(R.string.notification_channel_kpi_change), NotificationManager.IMPORTANCE_DEFAULT)
            val channelWorker = NotificationChannel(NOTIFICATION_CHANNEL_WORKER, getString(R.string.notification_channel_worker), NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager  = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channelKPIChanged)
            notificationManager.createNotificationChannel(channelWorker)
        }

        val updateWorker = PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).apply {
            cancelAllWork()
            //enqueueUniquePeriodicWork(WORKER_TAG,ExistingPeriodicWorkPolicy.KEEP,updateWorker)
        }
    }
}
package com.example.vrnandr.kpiwatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CHANNEL_ID = "kpi_change"

class KpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = getString(R.string.notification_channel)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            val notificationManager  = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val updateWorker = PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(updateWorker)
    }
}
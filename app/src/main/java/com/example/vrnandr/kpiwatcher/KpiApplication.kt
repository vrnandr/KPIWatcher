package com.example.vrnandr.kpiwatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.logger.MyDebugTree
import com.example.vrnandr.kpiwatcher.logger.MyFileLoggerTree
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CHANNEL_KPI_CHANGE = "Изменение КПЭ"
//const val NOTIFICATION_CHANNEL_WORKER = ""
const val WORKER_TAG = "updateKPI"

class KpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelKPIChanged = NotificationChannel(NOTIFICATION_CHANNEL_KPI_CHANGE, getString(R.string.notification_channel_kpi_change), NotificationManager.IMPORTANCE_DEFAULT)
            //val channelWorker = NotificationChannel(NOTIFICATION_CHANNEL_WORKER, getString(R.string.notification_channel_worker), NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager  = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channelKPIChanged)
            //notificationManager.createNotificationChannel(channelWorker)
        }

        val repo = Repository.get()
        val updateWorker = PeriodicWorkRequestBuilder<UpdateWorker>(repo.getTimer(), TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).apply {
            cancelAllWork()
            enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP,updateWorker)
        }

        if(BuildConfig.DEBUG){
            Timber.plant(MyDebugTree())
        }
        Timber.plant(MyFileLoggerTree())
    }
}

//TODO
// - настройки
// - детализация
// - убрать запуск воркера при запуске приложения (перенести в запуск фрагмента?)
// - детализация с первого числа месяца
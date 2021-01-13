package com.example.vrnandr.kpiwatcher.worker

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vrnandr.kpiwatcher.NOTIFICATION_CHANNEL_KPI_CHANGE
import com.example.vrnandr.kpiwatcher.NOTIFICATION_CHANNEL_WORKER
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.repository.Repository
import java.io.File
import java.util.*

const val REQUEST_CLOSE_PHRASE = "Статус изменен на \\\"Выполнен\\\""
const val TAG ="my"

class UpdateWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val repo = Repository.get()
    override fun doWork(): Result {
        Log.d(TAG, "doWork: run work")

        /*val notifacation = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_WORKER)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle(context.resources.getString(R.string.worker))
                .setContentText("")
                .setContentIntent(null)
                .build()
        NotificationManagerCompat.from(context).notify(1, notifacation)*/

        val hour = (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY)
        Log.d(TAG, "doWork: час: $hour")
        if (hour in 8..19){
            Log.d(TAG, "doWork: run kpiRequest")
            repo.kpiRequest()
        }
        return Result.success()
    }
}
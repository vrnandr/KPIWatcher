package com.example.vrnandr.kpiwatcher.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vrnandr.kpiwatcher.repository.Repository
import timber.log.Timber
import java.util.*

const val REQUEST_CLOSE_PHRASE = "Статус изменен на \\\"Выполнен\\\""

class UpdateWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val repo = Repository.get()
    override fun doWork(): Result {
        Timber.d("doWork: run work")

        /*val notifacation = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_WORKER)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle(context.resources.getString(R.string.worker))
                .setContentText("")
                .setContentIntent(null)
                .build()
        NotificationManagerCompat.from(context).notify(1, notifacation)*/

        val hour = (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY)
        Timber.d("doWork: час: $hour")
        if (hour in 8..19){
            Timber.d("doWork: run kpiRequest")
            repo.kpiRequest()
        }
        return Result.success()
    }
}
package com.example.vrnandr.kpiwatcher.worker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vrnandr.kpiwatcher.repository.Repository
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CLOSE_PHRASE = "Статус изменен на \\\"Выполнен\\\""
private const val OSK_DIRECTORY="OSKMobile"
private const val MINUTES_TO_UPDATE_KPI_ON_SITE = 16

class UpdateWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val repo = Repository.get()
    override fun doWork(): Result {
        Timber.d("run worker")
        val hour = (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY)
        val day = (Calendar.getInstance()).get(Calendar.DAY_OF_WEEK)
        val readPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (day in Calendar.MONDAY..Calendar.FRIDAY){
            if (repo.useLogFile()){
                if (!readPermission){
                    Timber.d("no permission to read SD")
                    return Result.success()
                }
                val lastString = lastDoneString()
                if (lastString!=null || hour==8){ // обновляем если есть запись в лог файле о закрытом запросе и утром с 8:00 до 9:00
                    Timber.d("run kpiRequest on change log file")
                    if (repo.kpiRequest())
                        repo.setLastString(lastString)
                }
            } else {
                if (hour in 8..19){ // обновляем с 8 утра до 8 вечера
                    Timber.d("run schedule kpiRequest")
                    repo.kpiRequest()
                }
            }
        } else
            Timber.d("Weekend! Don't update KPI")
        return Result.success()
    }

    private fun lastDoneString():String?{
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            try {
                val sdcard = Environment.getExternalStorageDirectory().absolutePath
                val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                val myFile = File("$sdcard/$OSK_DIRECTORY","${date}ServiceLog.log")
                val strings = myFile.readLines()
                var hours: Int? = null
                var minutes: Int? = null
                var lastString = ""
                for (s in strings){
                    if (s.contains(REQUEST_CLOSE_PHRASE)){
                        val pos = s.indexOf("PROTOCOLDATE")+15+11
                        hours = s.substring(pos,pos+2).toIntOrNull()
                        minutes = s.substring(pos+3,pos+5).toIntOrNull()
                        lastString = s
                    }
                }
                //Timber.d("time: $hours:$minutes  find string: $lastString")
                //если есть сохраненная строка и она отличается от последней найденной то
                if (lastString==repo.getLastString())
                    return null
                if (hours != null && minutes != null) {
                    val currentHour = (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY)
                    val currentMinute = (Calendar.getInstance()).get(Calendar.MINUTE)
                    val timeZoneMinuteOffset = Calendar.getInstance().timeZone.rawOffset/(1000*60)
                    val currentTotalMinutes = currentHour * 60 + currentMinute
                    val totalMinutes = hours * 60 + minutes
                    //Timber.d("$currentTotalMinutes >? $totalMinutes + $timeZoneMinuteOffset + $MINUTES_TO_UPDATE_KPI_ON_SITE")
                    if (currentTotalMinutes > totalMinutes + timeZoneMinuteOffset + MINUTES_TO_UPDATE_KPI_ON_SITE) {
                        Timber.d("Update time!")
                        return lastString
                    }
                } else
                    Timber.e("Error on parsing time \"$hours:$minutes\"")
            } catch (e:Exception){
                Timber.e("Error on: ${e.message}")
            }
        } else {
            Timber.e("SD card not accessible: ${Environment.getExternalStorageState()}")
        }
        return null
    }
}
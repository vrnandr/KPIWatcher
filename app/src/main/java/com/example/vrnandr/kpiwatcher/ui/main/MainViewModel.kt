package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Environment
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.worker.REQUEST_CLOSE_PHRASE
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val MINUTES_TO_UPDATE_KPI_ON_SITE = 16
const val OSK_DIRECTORY="OSKMobile"

@Suppress("UNUSED_PARAMETER")
class MainViewModel: ViewModel() {
    private val repo = Repository.get()
    val responseKPE = repo.responseKPE
    val showErrorToast = repo.showErrorToast

    val currentKpi : LiveData<Kpi> = repo.currentKPI

    fun onKPIButtonClick (view : View){
       repo.kpiRequest()
    }

    private val _time = MutableLiveData<String>()
    val time: LiveData<String>
        get() = _time

    private val _messageToShow = MutableLiveData<String>()
    val messageToShow: LiveData<String>
        get() = _messageToShow

    fun onStopWorkerClick (view: View){
        _time.postValue(System.currentTimeMillis().toString())
    }

    fun onClick (view: View){
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            try {
                val sdcard = Environment.getExternalStorageDirectory().absolutePath
                /*val dir = File("$sdcard/$OSK_DIRECTORY").listFiles()
                dir?.let {
                    val dirToView = arrayListOf<String>()
                    for (file in dir)
                        dirToView.add(file.name)
                    Timber.d("onClick: dir: $dirToView")
                    _messageToShow.postValue("DIR:$dirToView")
                }*/
                val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                Timber.d("onClick: current date is $date")
                val myFile = File("$sdcard/$OSK_DIRECTORY","${date}ServiceLog.log")
                //val myFile = File(Environment.getExternalStorageDirectory().absolutePath +"/OSKMobile","11-01-2021ServiceLog.log")
                val strings = myFile.readLines()
                var hours: Int? = null
                var minutes: Int? = null
                for (s in strings){
                    if (s.contains(REQUEST_CLOSE_PHRASE)){
                        Timber.d("file: all: $s")
                        val pos = s.indexOf("PROTOCOLDATE")+15+11
                        hours = s.substring(pos,pos+2).toIntOrNull()
                        minutes = s.substring(pos+3,pos+5).toIntOrNull()
                        Timber.d("file: sub:$hours $minutes")
                        _messageToShow.postValue("$hours $minutes")
                    }
                }

                if (hours != null && minutes != null) {
                    val currentHour = (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY)
                    val currentMinute = (Calendar.getInstance()).get(Calendar.MINUTE)
                    val timeZoneMinuteOffset = Calendar.getInstance().timeZone.rawOffset/(1000*60)
                    val currentTotalMinutes = currentHour * 60 + currentMinute
                    val totalMinutes = hours * 60 + minutes
                    if (currentTotalMinutes > totalMinutes + timeZoneMinuteOffset + MINUTES_TO_UPDATE_KPI_ON_SITE) {
                        Timber.d("onClick: Время обновлять!")
                        _messageToShow.postValue("Время обновлять!")
                    }
                }
            } catch (e:Exception){
                Timber.d("Error on: ${e.message}")
                _messageToShow.postValue("Error on read log files: "+e.message)
            }


        } else {
            Timber.d("SD card not accessible: ${Environment.getExternalStorageState()}")
        }
    }

    data class ParsedKPI (val value: String, val color: String, val text: String)

    fun convertKPI(raw:String): List<ParsedKPI> {
        val returnValue = mutableListOf<ParsedKPI>()
        for (s in raw.split(":")){
            var value = s.substringBefore(" ")
            val color = s.substringAfter(" ").substringBefore(" ")
            val text = s.substringAfter(" ").substringAfter(" ")
            if (value.length==7) //если строка типа 98.5547 то приводим к виду 98.55
                value = value.dropLast(2)
            returnValue.add(ParsedKPI(value,color,text))
        }
        return  returnValue
    }

    private val _parsedKPI= MutableLiveData<List<ParsedKPI>>()
    val parsedKPI: LiveData<List<ParsedKPI>>
        get() = _parsedKPI

    val kpiToParse = MutableLiveData<String>()

}
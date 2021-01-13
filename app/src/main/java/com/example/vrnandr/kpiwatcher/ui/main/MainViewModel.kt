package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Environment
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.TAG
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.worker.REQUEST_CLOSE_PHRASE
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("UNUSED_PARAMETER")
class MainViewModel() :ViewModel() {
    val repo = Repository.get()
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
                val myFile = File(Environment.getExternalStorageDirectory().absolutePath +"/OSKMobile","14-12-2020ServiceLog.log")
                //val myFile = File(Environment.getExternalStorageDirectory().absolutePath +"/OSKMobile","11-01-2021ServiceLog.log")
                val strings = myFile.readLines()
                for (s in strings){
                    if (s.contains(REQUEST_CLOSE_PHRASE)){
                        Log.d(TAG, "file: all: $s")
                        val pos = s.indexOf("PROTOCOLDATE")+15+11
                        val hours = s.substring(pos,pos+2)
                        val minutes = s.substring(pos+3,pos+5)
                        val totalMinutes = hours.toInt()*60+minutes.toInt()
                        Log.d(TAG, "file: sub:$hours $minutes")
                        _messageToShow.postValue("$hours $minutes")
                        //_time.postValue(ss)
                    }
                }
            } catch (e:Exception){
                Log.d(TAG, "Error on: "+e.message)
                _messageToShow.postValue("Error on read log files: "+e.message)
            }


        } else {
            Log.d(TAG, "doWork: SD card not accessible: "+ Environment.getExternalStorageState())
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
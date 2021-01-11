package com.example.vrnandr.kpiwatcher.ui.main

import android.view.View
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
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

    fun onClick (view: View){
        _time.postValue(System.currentTimeMillis().toString())
        /*if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val myFile = File(Environment.getExternalStorageDirectory().absolutePath +"/OSKMobile","14-12-2020ServiceLog.log")
            val strings = myFile.readLines()
            for (s in strings){
                if (s.contains(REQUEST_CLOSE_CODE)){
                    val pos = s.indexOf("PROTOCOLDATE")+15
                    val ss = s.substring(pos,19)
                    Log.d(TAG, "doWork: time: $ss")
                    _time.postValue(ss)
                }
            }

        } else {
            Log.d(TAG, "doWork: SD card not accessible: "+ Environment.getExternalStorageState())
        }*/

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
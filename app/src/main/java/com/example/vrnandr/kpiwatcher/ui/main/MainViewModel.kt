package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Environment
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.worker.REQUEST_CLOSE_PHRASE
import kotlinx.android.synthetic.main.main_fragment.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val MINUTES_TO_UPDATE_KPI_ON_SITE = 16
private const val OSK_DIRECTORY="OSKMobile"

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
        when(view.id){
            R.id.useLogFile -> {
                repo.setUseLogFile((view as CheckBox).isChecked)
            }
        }
    }

    val useLogFile = repo.getUseLogFile()

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
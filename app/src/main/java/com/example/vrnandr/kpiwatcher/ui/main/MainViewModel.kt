package com.example.vrnandr.kpiwatcher.ui.main

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("UNUSED_PARAMETER")
class MainViewModel() :ViewModel() {
    val repo = Repository.get()
    val responseKPE = repo.responseKPE
    val showErrorToast = repo.showErrorToast

    val currentKpi : LiveData<Kpi> = repo.currentKPI

    fun onKPIButtonClick (view : View){
       repo.kpiRequest()
    }

//    private val api = Api(getApplication())


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
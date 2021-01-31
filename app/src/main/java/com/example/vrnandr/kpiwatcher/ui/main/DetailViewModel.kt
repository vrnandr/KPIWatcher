package com.example.vrnandr.kpiwatcher.ui.main

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.utility.convertKPI
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel :ViewModel() {

    fun getData(): LineData = runBlocking{
        val repo = Repository.get()
        val entriesZ = arrayListOf<Entry>()
        val entriesS = arrayListOf<Entry>()
        val result = async { repo.userKPI() }
        val list = result.await()
        if (list.isNotEmpty()){
            for (entry in list){
                val timestamp = entry.timestamp
                val kpi = convertKPI(entry.kpi)
                for (k in kpi){
                    when(k.text){
                        "Загруженность" -> entriesZ.add(Entry(timestamp.toFloat(),k.value.toFloat()))
                        "Итоговый коэффициент" -> entriesS.add(Entry(timestamp.toFloat(),k.value.toFloat()))
                    }
                }
            }
            val dataSetZ = LineDataSet(entriesZ,"Загруженность")
            dataSetZ.color = Color.RED
            val dataSetS = LineDataSet(entriesS,"Итоговый коэффициент")
            dataSetS.color = Color.BLUE
            val dataSet = arrayListOf<ILineDataSet>(dataSetS,dataSetZ)

            return@runBlocking LineData(dataSet)
        } else
            return@runBlocking LineData()
    }

    class MyXAxisFormatter: ValueFormatter(){
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return SimpleDateFormat("dd-MM HH:mm", Locale.getDefault()).format(Date(value.toLong()))
        }
    }
}
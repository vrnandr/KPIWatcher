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
import kotlin.collections.ArrayList

class DetailViewModel :ViewModel() {

    val colors = intArrayOf(Color.BLACK,Color.BLUE,Color.RED,Color.MAGENTA, Color.YELLOW,Color.GREEN, Color.DKGRAY)
    val kpisTitle = mutableSetOf<String>()
    private val repo = Repository.get()
    val visibleKPI = repo.getChartKPI()

    fun getData(): LineData = runBlocking{

        val result = async { repo.userKPI() }
        val list = result.await()

        if (list.isNotEmpty()){

            val map = mutableMapOf<String,ArrayList<Entry>>()

            for (entry in list){
                val timestamp = entry.timestamp
                val kpi = convertKPI(entry.kpi)
                for (k in kpi){
                    if (map.keys.contains(k.text)){
                        val aaa = map.getValue(k.text)
                        aaa.add(Entry(timestamp.toFloat(),k.value.toFloat()))
                        map[k.text] = aaa
                    } else
                        map[k.text] = arrayListOf(Entry(timestamp.toFloat(),k.value.toFloat()))
                }
            }
            //Timber.d(map.toString())
            kpisTitle.addAll(map.keys)

            val dataSet = arrayListOf<ILineDataSet>()
            var i = 0
            for (e in map){
                val lds = LineDataSet(e.value,e.key)
                lds.color = colors[i++ % colors.size]
                lds.isVisible = visibleKPI.contains(e.key)
                dataSet.add(lds)
            }

            return@runBlocking LineData(dataSet)
        } else
            return@runBlocking LineData()
    }

    fun saveChartKPI(chartKPI: String){
        repo.setChartKPI(chartKPI)
    }

    class MyXAxisFormatter: ValueFormatter(){
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return SimpleDateFormat("dd-MM HH:mm", Locale.getDefault()).format(Date(value.toLong()))
        }
    }
}
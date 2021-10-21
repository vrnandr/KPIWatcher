package com.example.vrnandr.kpiwatcher.ui.main

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
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

    private val colors = intArrayOf(Color.GREEN,Color.CYAN,Color.RED,Color.MAGENTA, Color.YELLOW,Color.BLUE, Color.DKGRAY)
    val kpisTitle = mutableSetOf<String>()
    private val repo = Repository.get()
    fun visibleKPI() = repo.getChartKPI()

    fun getData(): LineData = runBlocking{

        //val result = async { repo.userKPI() }
        val list =
            withContext(Dispatchers.IO) { repo.userKPI() }

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
            val visibleKPIs = visibleKPI()
            for (e in map){
                val lds = LineDataSet(e.value,e.key)
                lds.color = colors[i++ % colors.size]
                if (visibleKPIs.isNotBlank())
                    lds.isVisible = visibleKPIs.contains(e.key)
                dataSet.add(lds)
            }

            return@runBlocking LineData(dataSet)
        } else
            return@runBlocking LineData()
    }

    fun saveChartKPI(chartKPI: String){
        repo.setChartKPI(chartKPI)
    }

    fun addData(){
        runBlocking {
            repo.addKpi(Kpi(1627643639391, "0000001091","100 green Итоговый коэффициент:100 green Добросовестность:88.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1627543639391, "0000001091","100 green Итоговый коэффициент:100 green Добросовестность:88.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1627443639391, "0000001091","100 green Итоговый коэффициент:100 green Добросовестность:88.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1627343639391, "0000001091","100 green Итоговый коэффициент:100 green Добросовестность:88.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1627243639391, "0000001091","85 green Итоговый коэффициент:100 green Добросовестность:78.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1627143639391, "0000001091","85 green Итоговый коэффициент:100 green Добросовестность:68.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1627043639391, "0000001091","70 green Итоговый коэффициент:100 green Добросовестность:58.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1626943639391, "0000001091","70 green Итоговый коэффициент:100 green Добросовестность:58.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1626843639391, "0000001091","85 green Итоговый коэффициент:100 green Добросовестность:65.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1626743639391, "0000001091","70 green Итоговый коэффициент:100 green Добросовестность:50.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1626643639391, "0000001091","85 green Итоговый коэффициент:100 green Добросовестность:75.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
            repo.addKpi(Kpi(1626543639391, "0000001091","85 green Итоговый коэффициент:100 green Добросовестность:70.6973 green Загруженность:99.867 green Комплексный показатель:95.7447 red Своевременность:78.7234 green Оперативность:100 green Качество работы:100 green Удовлетворенность:100 green Своевременность ППР"))
        }
    }

    class MyXAxisFormatter: ValueFormatter(){
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return SimpleDateFormat("dd-MM", Locale.getDefault()).format(Date(value.toLong()))
        }
    }
}
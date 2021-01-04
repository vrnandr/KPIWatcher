package com.example.vrnandr.kpiwatcher.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.repository.database.KpiDatabase
import com.example.vrnandr.kpiwatcher.repository.network.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(context: Context) {
    companion object{
        private var INSTANCE: Repository? = null

        fun initialize (context: Context){
            if (INSTANCE == null)
                INSTANCE = Repository(context)
        }

        fun get():Repository{
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }

    private val dao by lazy { KpiDatabase.getInstance(context.applicationContext).kpiDao  }
    private val networkApi by lazy { Api(context as Application) }


    val currentKPI = dao.getCurrentKPI()
    val allKpi = dao.getAllKPI()
    suspend fun addKpi(kpi:Kpi) = dao.addKPI(kpi)

    private fun clearCookies() = networkApi.clearCookies()


    //<------
    private val _showErrorToast = MutableLiveData<String>()
    val showErrorToast: LiveData<String>
        get() = _showErrorToast

    private val _responseKPE = MutableLiveData<String>()
    val responseKPE: LiveData<String>
        get() = _responseKPE


    private var reopenLoginPage = true
    private var _csrf:String =""
    fun openLoginPage() {
        clearCookies()
        networkApi.retrofitService.login().enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _showErrorToast.value = "Failure on open login page:"+t.message
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result :String? = response.body()
                result?.let {
                    try{
                        val doc = Jsoup.parse(it)
                        val elements = doc.getElementsByTag("meta")
                        for (element in elements)
                            if (element.attr("name")=="csrf-token") {
                                //_response.value = element.attr("content")
                                _csrf=element.attr("content")
                                login()
                            }
                    } catch (e:Exception){
                        _showErrorToast.value = "Error on parse login page HTML: ${e.message}"
                    }

                }
            }

        })
    }

    fun login() {
        networkApi.retrofitService.loginrequest(_csrf,"0000001091","ВАРАНКИНАНДРЕЙАЛЕКСЕЕВИЧ","1").enqueue(object :
                Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _showErrorToast.value = "Failure on login:"+t.message
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result :String? = response.body()
                result?.let {
                    try {
                        val doc = Jsoup.parse(it)
                        val elements = doc.getElementsByClass("btn btn-link logout")
                        val list = mutableListOf<String>()
                        for (element in elements)
                            list.add(element.text())
                        //_responseOnLogin.value = list.toString()
                        kpiRequest()
                    } catch (e:Exception){
                        _showErrorToast.value = "Error on parse main page HTML: ${e.message}"
                    }
                }
            }
        })
    }

    fun kpiRequest(){
        networkApi.retrofitService.dashboard().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result: String? = response.body()
                    result?.let {
                        try {
                            val doc = Jsoup.parse(it)
                            val who = doc.select("h1.hyphens+p").first().text()
                            val about = doc.select("h1.hyphens+p").next().text()
                            val elements = doc.select("div.circle-chart")
                            val data = elements.eachAttr("data-value")
                            val color = elements.eachAttr("data-color")
                            val names = elements.next().eachText()
                            var kpiString = ""
                            if (data.count() == names.count()) {
                                for (i in 0 until data.count())
                                    kpiString += "${data[i]} ${color[i]} ${names[i]}:"
                                val kpi = Kpi(System.currentTimeMillis(), "0000001091", kpiString.dropLast(1))
                                Log.d("my", "onResponse: $kpi")
                                if (kpiString.isNotEmpty())
                                    CoroutineScope(Dispatchers.IO).launch {
                                        addKpi(kpi)
                                    }
                            }
                            _responseKPE.value = "$who\n$about"
                        } catch (i: IndexOutOfBoundsException) {
                            _showErrorToast.value = "Error on parse HTML: " + i.message
                        } catch (e: Exception) {
                            _showErrorToast.value = "Error on parse HTML: " + e.message
                            if (reopenLoginPage) {
                                reopenLoginPage = false
                                openLoginPage()
                            }

                        }
                    }
                } else {
                    _showErrorToast.value = "Response unsuccessful: " + response.message()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _responseKPE.value = "Failure:" + t.message
            }


        })
    }


    //------->
}
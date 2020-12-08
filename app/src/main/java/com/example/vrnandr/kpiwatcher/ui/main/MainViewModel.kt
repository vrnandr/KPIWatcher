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
class MainViewModel(val repo: Repository) :ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _responseOnLogin = MutableLiveData<String>()
    val responseOnLogin: LiveData<String>
        get() = _responseOnLogin

    private val _responseKPE = MutableLiveData<String>()
    val responseKPE: LiveData<String>
        get() = _responseKPE

    private val _showErrorToast = MutableLiveData<String>()
    val showErrorToast: LiveData<String>
        get() = _showErrorToast

    val currentKpi : LiveData<Kpi> = repo.currentKPI

    fun onKPIButtonClick (view : View){
        kpiRequest()
    }

//    private val api = Api(getApplication())

    private var reopenLoginPage = true
    private var _csrf:String =""
    fun openLoginPage() {
        repo.clearCookies()
        repo.api.retrofitService.login().enqueue(object : Callback<String> {
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
        repo.api.retrofitService.loginrequest(_csrf,"0000001091","ВАРАНКИНАНДРЕЙАЛЕКСЕЕВИЧ","1").enqueue(object :
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
        repo.api.retrofitService.dashboard().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    val result :String? = response.body()
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
                            if (data.count()==names.count()){
                                for ( i in 0 until data.count())
                                    kpiString +="${data[i]} ${color[i]} ${names[i]}:"
                                val kpi = Kpi(System.currentTimeMillis(),"0000001091", kpiString.dropLast(1))
                                if (kpiString.isNotEmpty())
                                    viewModelScope.launch {
                                        repo.addKpi(kpi)
                                    }
                            }
                            _responseKPE.value = "$who\n$about"
                        }catch (i: IndexOutOfBoundsException){
                            _showErrorToast.value ="Error on parse HTML: " + i.message
                        }catch (e: Exception){
                            _showErrorToast.value ="Error on parse HTML: " + e.message
                            if (reopenLoginPage){
                                reopenLoginPage = false
                                openLoginPage()
                            }

                        }
                    }
                }
                else {
                    _showErrorToast.value = "Response unsuccessful: "+response.message()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _responseKPE.value = "Failure:"+t.message
            }


        })
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
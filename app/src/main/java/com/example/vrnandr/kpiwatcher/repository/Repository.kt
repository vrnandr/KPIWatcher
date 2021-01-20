package com.example.vrnandr.kpiwatcher.repository

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vrnandr.kpiwatcher.NOTIFICATION_CHANNEL_KPI_CHANGE
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.repository.database.KpiDatabase
import com.example.vrnandr.kpiwatcher.repository.network.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

//const val USER_TAB_NUM = "0000000520"
//const val USER_FIO = "СУШЕНЦОВАЛЕКСЕЙИВАНОВИЧ"
//const val USER_TAB_NUM = "0000001091"
//const val USER_FIO = "ВАРАНКИНАНДРЕЙАЛЕКСЕЕВИЧ"
private const val LOGIN = "login"
private const val PASSWORD = "password"
private const val LOGIN_SUCCESSFUL = "Выйти"
private const val LOGIN_FAILURE = "Incorrect username or password"

private const val CREDENTIALS = "credentials"

class Repository private constructor(val context: Context) {
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

    private val sp = context.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE)

    private val dao by lazy { KpiDatabase.getInstance(context.applicationContext).kpiDao  }
    private val networkApi by lazy { Api(context as Application) }

    val currentKPI = dao.getCurrentKPI()
    val allKpi = dao.getAllKPI()
    suspend fun addKpi(kpi:Kpi) = dao.addKPI(kpi)

    private fun clearCookies() {
        networkApi.clearCookies()
    }

    fun saveCredentials (login: String?, password: String?){
        sp.edit().putString(LOGIN,login).putString(PASSWORD,password).apply()
    }
    fun getLogin():String?{
        return sp.getString(LOGIN,null)
    }
    private fun getPassword():String?{
        return sp.getString(PASSWORD,null)
    }
    fun deleteCredentials(){
        sp.edit().putString(LOGIN,null).putString(PASSWORD,null).apply()
    }

    //<------
    private val _showErrorToast = MutableLiveData<String>()
    val showErrorToast: LiveData<String>
        get() = _showErrorToast

    private val _responseKPE = MutableLiveData<String>()
    val responseKPE: LiveData<String>
        get() = _responseKPE

    private val _successLogin = MutableLiveData<Boolean>()
    val successLogin: LiveData<Boolean>
        get() = _successLogin


    private var reopenLoginPage = true
    private var _csrf:String =""
    fun openLoginPage(login: String, password: String) {
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
                                _csrf=element.attr("content")
                                 login(login,password)
                            }
                    } catch (e:Exception){
                        _showErrorToast.value = "Error on parse login page HTML: ${e.message}"
                    }
                }
            }
        })
    }

    fun login(login: String, password: String) {
        networkApi.retrofitService.loginrequest(_csrf, login, password,"1").enqueue(object :
                Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _showErrorToast.value = "Failure on login:"+t.message
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result :String? = response.body()
                result?.let {
                    if (result.contains(LOGIN_SUCCESSFUL)){
                        _successLogin.postValue(true)
                        saveCredentials(login,password)
                        kpiRequest()
                    }
                    else if (result.contains(LOGIN_FAILURE)){
                        _successLogin.postValue(false)
                        deleteCredentials()
                        _showErrorToast.value = "Login error"
                    }

                }
            }
        })
    }

    fun kpiRequest(){
        val login = getLogin()
        val password = getPassword()
        if (!login.isNullOrBlank()&&!password.isNullOrBlank()){
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
                                if (data.count() == names.count() && names.count() == color.count()) {
                                    var kpiString = ""
                                    var kpiNotificationString = ""
                                    for (i in 0 until data.count()){
                                        kpiString += "${data[i]} ${color[i]} ${names[i]}:"
                                        kpiNotificationString += "${data[i]} ${names[i]}:"
                                    }
                                    kpiString = kpiString.dropLast(1)
                                    Timber.d("onResponse: $kpiString :::: ${currentKPI.value?.kpi}")
                                    if (currentKPI.value?.kpi != kpiString && kpiString.isNotEmpty()) {
                                        kpiNotificationString = kpiNotificationString.dropLast(1)
                                        kpiNotificationString = kpiNotificationString.replace(":","\n")
                                        Timber.d("onResponse: insert kpi and notify user")

                                        val kpi = Kpi(System.currentTimeMillis(), login, kpiString)
                                        CoroutineScope(Dispatchers.IO).launch { addKpi(kpi) }
                                        val colorString = kpiString.substringAfter(" ").substringBefore(" ")
                                        var notificationIconColor = Color.GREEN
                                        when (colorString) {
                                            "orange" -> notificationIconColor = Color.parseColor("#FFA500")
                                            "red" -> notificationIconColor = Color.RED
                                        }
                                        val notification = NotificationCompat
                                                .Builder(context, NOTIFICATION_CHANNEL_KPI_CHANGE)
                                                .setSmallIcon(R.drawable.ic_circle)
                                                .setContentTitle(context.resources.getString(R.string.kpi_changed))
                                                .setContentText(kpiNotificationString)
                                                .setColor(notificationIconColor)
                                                .setStyle(NotificationCompat.BigTextStyle()
                                                        .bigText(kpiNotificationString))
                                                .build()
                                        NotificationManagerCompat.from(context).notify(0, notification)
                                    } else{
                                        Timber.d("onResponse: kpi equals, not insert")
                                    }

                                }
                                _responseKPE.value = "$who\n$about"
                            } catch (i: IndexOutOfBoundsException) {
                                Timber.d("onResponse: Error on parse HTML: ${i.message}")
                                _showErrorToast.value = "Error on parse HTML: " + i.message
                            } catch (e: Exception) {
                                Timber.d("onResponse: Error on parse HTML: ${e.message}")
                                _showErrorToast.value = "Error on parse HTML: " + e.message
                                if (reopenLoginPage) {
                                    reopenLoginPage = false
                                    openLoginPage(login,password)
                                }
                            }
                        }
                    } else {
                        Timber.d("onResponse: Response unsuccessful: ${response.message()}")
                        _showErrorToast.value = "Response unsuccessful: " + response.message()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.d("onFailure: Failure: ${t.message}")
                    _responseKPE.value = "Failure:" + t.message
                }


            })
        } else {
            _showErrorToast.value = "Login or password is NULL or blank, kpi request not permitted \n Logout and login again"
        }

    }
    //------->


}

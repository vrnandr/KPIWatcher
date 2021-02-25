package com.example.vrnandr.kpiwatcher.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.repository.database.KpiDatabase
import com.example.vrnandr.kpiwatcher.repository.network.Api
import com.example.vrnandr.kpiwatcher.utility.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

private const val LOGIN = "login"
private const val PASSWORD = "password"
private const val LOGIN_SUCCESSFUL = "Выйти"
private const val LOGIN_WORD = "Вход"
private const val LOGIN_FAILURE = "Incorrect username or password"
private const val KPI_NOT_FOUND ="КПЭ не найдены"

private const val CREDENTIALS = "credentials"
private const val SETTINGS = "settings"
private const val USE_LOG_FILE = "use_log_file"
private const val ENABLE_LOGGING = "enable_logging"
private const val REFRESH_METHOD = "refresh_method"

private const val LAST_FIND_STRING = "last_find_string"
private const val TIMER = "timer"
private const val ABOUT = "about"

private const val TIMER_ON_LOG_FILE = 15L //минуты при обновлении по анализу лога МС
const val DEFAULT_TIMER_LONG = 60L //минуты при обновлении по расписанию
private const val DEFAULT_TIMER_STRING = "60"

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

    private val spCredentials = context.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE)
    //private val spSettings = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
    private val spSettings = PreferenceManager.getDefaultSharedPreferences(context)

    private val dao by lazy { KpiDatabase.getInstance(context.applicationContext).kpiDao  }
    private val networkApi by lazy { Api(context as Application) }

    val liveDataCurrentKPI = dao.getLiveDataCurrentKPI()
    suspend fun currentKPI() = dao.getCurrentKPI()
    suspend fun addKpi(kpi:Kpi) = dao.addKPI(kpi)


    suspend fun userKPI():List<Kpi> {
        //timestamp начала месяца, чтобы запросить данные за текущий месяц
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            clear(Calendar.MINUTE)
            clear(Calendar.SECOND)
            clear(Calendar.MILLISECOND)
            set(Calendar.DAY_OF_MONTH, 1)
        }
         return dao.getKPI(getLogin()?:"0000000000",cal.timeInMillis)
    }


    private fun clearCookies() {
        networkApi.clearCookies()
    }

    fun saveCredentials (login: String?, password: String?){
        spCredentials.edit().putString(LOGIN,login).putString(PASSWORD,password).apply()
    }
    fun getLogin():String?{
        return spCredentials.getString(LOGIN,null)
    }
    private fun getPassword():String?{
        return spCredentials.getString(PASSWORD,null)
    }
    fun deleteCredentials(){
        spCredentials.edit().putString(LOGIN,null).putString(PASSWORD,null).apply()
    }

    //TODO remove on settings
    /*fun setUseLogFile(useLogFile:Boolean){
        if (useLogFile)
            spSettings.edit().putLong(TIMER, TIMER_ON_LOG_FILE).apply()
        else
            spSettings.edit().putLong(TIMER, DEFAULT_TIMER_LONG).apply()
        spSettings.edit().putBoolean(USE_LOG_FILE,useLogFile).apply()
    }*/

    fun useLogFile():Boolean{
        val refreshMethod = spSettings.getString(REFRESH_METHOD,null)
        return refreshMethod == "log_file"
        //return spSettings.getBoolean(ENABLE_LOGGING,false)
    }
    fun getTimer():Long{
        val strTimer = spSettings.getString(TIMER, DEFAULT_TIMER_STRING)
        return strTimer?.toLongOrNull()?: DEFAULT_TIMER_LONG
    }

    fun useWorker():Boolean{
        val refreshMethod = spSettings.getString(REFRESH_METHOD,null)
        return refreshMethod == "log_file" || refreshMethod == "periodic"
    }

    fun enableLogging():Boolean{
        return spSettings.getBoolean(ENABLE_LOGGING,false)
    }

    fun setLastString(s: String?){
        spSettings.edit().putString(LAST_FIND_STRING,s).apply()
    }
    fun getLastString(): String? {
        return spSettings.getString(LAST_FIND_STRING,null)
    }

    fun setAbout(s: String?){
        spSettings.edit().putString(ABOUT,s).apply()
    }
    private fun getAbout(): String? {
        return spSettings.getString(ABOUT,null)
    }

    //<------
    private val _showErrorToast = MutableLiveData<String>()
    val showErrorToast: LiveData<String>
        get() = _showErrorToast

    private val _responseKPE = MutableLiveData<String>()
    val responseKPE: LiveData<String>
        get() = _responseKPE

    init {
        _responseKPE.value = getAbout()
    }


    private val _successLogin = MutableLiveData<Boolean>()
    val successLogin: LiveData<Boolean>
        get() = _successLogin

    private var reopenLoginPage = true
    private var _csrf:String =""
    fun openLoginPage(login: String, password: String) {
        clearCookies()
        deleteCredentials()
        networkApi.retrofitService.login().enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _showErrorToast.value = "Failure on open login page: ${t.message}"
                Timber.d("Failure on open login page: ${t.message}")
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
                                Timber.d("open login page success, do login")
                                login(login,password)
                            }
                    } catch (e:Exception){
                        _showErrorToast.value = "Error on parse login page HTML: ${e.message}"
                        Timber.d("Error on parse login page HTML: ${e.message}")
                    }
                }
            }
        })
    }

    fun login(login: String, password: String) {
        networkApi.retrofitService.loginrequest(_csrf, login, password,"1").enqueue(object :
                Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _showErrorToast.value = "Failure on login: ${t.message}"
                Timber.d("Failure on login: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val result :String? = response.body()
                result?.let {
                    if (result.contains(LOGIN_SUCCESSFUL)){
                        Timber.d("login success, save credentials, do kpi request")
                        _successLogin.postValue(true)
                        saveCredentials(login,password)
                        kpiRequest()
                    } else {
                        if (result.contains(LOGIN_FAILURE)) {
                            _successLogin.postValue(false)
                            deleteCredentials()
                            _showErrorToast.value = "Login error"
                            Timber.d("login unsuccessful")
                            Timber.d(result)
                        } else {
                            Timber.d("WTF it can`t be")
                            Timber.d(result)
                        }
                    }
                }
            }
        })
    }

    fun kpiRequest():Boolean{
        val login = getLogin()
        val password = getPassword()
        var success = false
        if (!login.isNullOrBlank()&&!password.isNullOrBlank()){
            networkApi.retrofitService.dashboard().enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val result: String? = response.body()
                        result?.let {
                            try {
                                //если в теле ответа есть текст "Вход" значит что-то пошло не так и надо снова выполнить логин, если нет парсим страницу
                                if (result.contains(LOGIN_WORD))
                                    openLoginPage(login,password)
                                val doc = Jsoup.parse(it)
                                val who = doc.select("h1.hyphens+p").first().text()
                                val about = doc.select("h1.hyphens+p").next().text()
                                if (!result.contains(KPI_NOT_FOUND)) {
                                    val elements = doc.select("div.circle-chart")
                                    val data = elements.eachAttr("data-value")
                                    val color = elements.eachAttr("data-color")
                                    val names = elements.next().eachText()
                                    if (data.count() == names.count() && names.count() == color.count()) {
                                        var kpiString = ""
                                        for (i in 0 until data.count())
                                            kpiString += "${data[i]} ${color[i]} ${names[i]}:"
                                        kpiString = kpiString.dropLast(1)

                                        val job =  CoroutineScope(Dispatchers.IO).async { currentKPI()?.kpi }
                                        runBlocking {
                                            val savedKPIString = job.await()
                                            //Timber.d("onResponse: $kpiString :::: $savedKPIString")
                                            if (savedKPIString != kpiString && kpiString.isNotEmpty()) {
                                                Timber.d("onResponse: insert kpi and notify user")
                                                notify(context, kpiString)
                                                val kpi = Kpi(System.currentTimeMillis(), login, kpiString)
                                                CoroutineScope(Dispatchers.IO).launch { addKpi(kpi) }
                                            } else{
                                                Timber.d("onResponse: kpi equals, not insert")
                                            }
                                        }
                                    }
                                    _responseKPE.value = "$who\n$about"
                                    setAbout("$who\n$about")
                                } else {
                                    _responseKPE.value = "$who\n$about\n$KPI_NOT_FOUND"
                                    setAbout("$who\n$about\n$KPI_NOT_FOUND")
                                }

                                success = true
                            } catch (e: Exception) {
                                Timber.e("onResponse: Error on parse HTML: ${e.message}")
                                _showErrorToast.value = "Error on parse HTML: " + e.message
                                /*if (reopenLoginPage) {
                                    reopenLoginPage = false
                                    openLoginPage(login,password)
                                }*/
                            }
                        }
                    } else {
                        Timber.e("onResponse: Response unsuccessful: ${response.message()}")
                        _showErrorToast.value = "Response unsuccessful: " + response.message()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.e("onFailure: Failure: ${t.message}")
                    _responseKPE.value = "Failure:" + t.message
                }
            })
        } else {
            _showErrorToast.value = "Login or password is NULL or blank, kpi request not permitted \n Logout and login again"
            Timber.d("Login or password is NULL or blank, kpi request not permitted \n Logout and login again")
        }
        return success
    }
    //------->

}

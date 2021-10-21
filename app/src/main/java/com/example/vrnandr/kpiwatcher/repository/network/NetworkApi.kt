package com.example.vrnandr.kpiwatcher.repository.network

import android.app.Application
import android.content.Context
import com.example.vrnandr.kpiwatcher.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import retrofit2.http.Headers
import timber.log.Timber
import java.util.concurrent.TimeUnit


private const val BASE_URL = "http://oskinfotrans.ru/infoportal/"
//private const val BASE_URL = "http://192.168.0.14/" //мебельная
//private const val BASE_URL = "http://192.168.0.71/" //толстого
//private const val BASE_URL = "http://10.184.199.164/" //работа
//private const val BASE_URL = "http://192.168.0.47:8080/" //пк

private const val PREF = "cookiesName"
private const val DOMAIN = "domain"
private const val COOKIE = "cookie"
private const val CONNECT_TIMEOUT = 20L
private const val READ_TIMEOUT = 30L

interface NetworkApi {
    @GET("index.php?r=site%2Flogin")
    fun login():Call<String>

    @GET("index.php?r=site%2Fdashboard")
    //@GET ("kpi100")
    //@GET ("kpi")
    //@GET ("blank")
    fun dashboard():Call<String>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("index.php?r=site%2Flogin")
    fun loginrequest(@Field("_csrf") csrf: String,
                     @Field("Login[username]") username: String,
                     @Field("Login[password]") password: String,
                     @Field("Login[rememberMe]") rememberMeString: String):Call<String>
}

class Api(private val application: Application) {

    val retrofitService :NetworkApi by lazy {

        val okHttpBuilder = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS)
                .cookieJar(SessionCookieJar(application))
        if(BuildConfig.DEBUG){
            val interceptor = HttpLoggingInterceptor { message -> Timber.tag("okHttp").d(message) }
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            //interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(interceptor)
        }
        val okHttpClient = okHttpBuilder.build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

        retrofit.create(NetworkApi::class.java) }

    fun clearCookies(){
        application.getSharedPreferences(COOKIE, Context.MODE_PRIVATE).edit().clear().apply()
    }
}

class SessionCookieJar(application: Application): CookieJar{
    private val sp = application.getSharedPreferences(COOKIE, Context.MODE_PRIVATE)
    private val speditor = sp.edit()
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        //TODO костыли [0]
        speditor.putString(DOMAIN, cookies[0].domain()).apply()
        val cookiesName = mutableSetOf<String>()
        for (cookie in cookies)
            cookiesName.add(cookie.name())
        speditor.putStringSet(PREF, cookiesName).apply()
        for (cookie in cookies) {
            speditor.putString(cookie.name(), cookie.value()).apply()
            speditor.putLong(cookie.name() + "_expires", cookie.expiresAt()).apply()
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val cookies = mutableListOf<Cookie>()
        val cookiesName = sp.getStringSet(PREF, mutableSetOf<String>())
        val domain = sp.getString(DOMAIN, "")
        if (cookiesName != null) {
            for (cookieName in cookiesName){
                val cookieValue = sp.getString(cookieName, "")
                val expires = sp.getLong(cookieName + "_expires", 0L)
                //Timber.d("loadForRequest: current:"+System.currentTimeMillis()+", expires $expires")
                if (System.currentTimeMillis()<expires)
                    cookies.add(Cookie.Builder().domain(domain).name(cookieName).value(cookieValue).build())
            }
        }
        return cookies
    }
}
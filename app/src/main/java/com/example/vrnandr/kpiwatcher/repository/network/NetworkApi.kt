package com.example.vrnandr.kpiwatcher.repository.network

import android.app.Application
import android.content.Context
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import retrofit2.http.Headers

//private const val BASE_URL = "http://oskinfotrans.ru/infoportal/"
private const val BASE_URL = "http://192.168.0.14/"
private const val PREF = "cookiesName"
private const val DOMAIN = "domain"
private const val COOKIE = "cookie"

interface NetworkApi {
    @GET ("index.php?r=site%2Flogin")
    fun login():Call<String>

    //@GET ("index.php?r=site%2Fdashboard")
    @GET ("kpi100")
    fun dashboard():Call<String>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST ("index.php?r=site%2Flogin")
    fun loginrequest(@Field("_csrf") csrf:String,
                     @Field("Login[username]") username:String,
                     @Field("Login[password]") password:String,
                     @Field("Login[rememberMe]") rememberMeString:String):Call<String>
}

class Api(val application: Application) {

    val retrofitService :NetworkApi by lazy {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).cookieJar(SessionCookieJar(application)).build()

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

class SessionCookieJar (val application: Application): CookieJar{
    private val sp = application.getSharedPreferences(COOKIE, Context.MODE_PRIVATE)
    private val speditor = sp.edit()
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        //TODO костыли [0]
        speditor.putString(DOMAIN,cookies[0].domain()).apply()
        val cookiesName = mutableSetOf<String>()
        for (cookie in cookies)
            cookiesName.add(cookie.name())
        speditor.putStringSet(PREF,cookiesName).apply()
        for (cookie in cookies) {
            speditor.putString(cookie.name(), cookie.value()).apply()
            speditor.putLong(cookie.name()+"_expires", cookie.expiresAt()).apply()
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val cookies = mutableListOf<Cookie>()
        val cookiesName = sp.getStringSet(PREF, mutableSetOf<String>())
        val domain = sp.getString(DOMAIN,"")
        if (cookiesName != null) {
            for (cookieName in cookiesName){
                val cookieValue = sp.getString(cookieName,"")
                val expires = sp.getLong(cookieName+"_expires",0L)
                //Log.d("my", "loadForRequest: current:"+System.currentTimeMillis()+", expires $expires")
                if (System.currentTimeMillis()<expires)
                    cookies.add(Cookie.Builder().domain(domain).name(cookieName).value(cookieValue).build())
            }
        }
        return cookies
    }
}
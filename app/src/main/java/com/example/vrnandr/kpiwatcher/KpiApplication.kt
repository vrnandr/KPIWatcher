package com.example.vrnandr.kpiwatcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.vrnandr.kpiwatcher.logger.MyDebugTree
import com.example.vrnandr.kpiwatcher.logger.MyFileLoggerTree
import com.example.vrnandr.kpiwatcher.repository.Repository
import timber.log.Timber

const val NOTIFICATION_CHANNEL_KPI_CHANGE = "Изменение КПЭ"
const val WORKER_TAG = "updateKPI"

class KpiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Repository.initialize(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelKPIChanged = NotificationChannel(NOTIFICATION_CHANNEL_KPI_CHANGE, getString(R.string.notification_channel_kpi_change), NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager  = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channelKPIChanged)
        }

        if (BuildConfig.DEBUG){
            Timber.plant(MyDebugTree())
        }


        //дерево садится в настройках при клике на логе
        val repo = Repository.get()
        val writePermission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (repo.enableLogging() && writePermission){
            Timber.plant(MyFileLoggerTree())
        }
    }
}

//TODO
// + настройки
// + детализация
// + детализация с первого числа месяца
// + в нотификации первое значение и не равные 100
// + в нотификации настроить интент
// + переход на bottom чего-то там
// + убрать отмену раннера при запуске приложения
// + разнести разрешения на чтение и запись, запрашивать при нажатии на соответ. кнопку
// + сохранять и выводить about из requestKPI()
// + удалились данные для входа при не выполненом запросе
// + иконки нотификации в зависимости от КПЭ
// + toast при не изменившихся КПЭ
// + добавить описание настроек
// + тосты показываются каждый раз при повороте экране, а должны только 1 раз при событии и все
// выбор периода графика
// + выбор что отображать на графике
// в начала меясца когда нет КПЭ отобржаются последние полученные КПЭ
// + при логине под другим первично отобраются КПЭ прошлого логина
// не красиво при ландшафтном виде
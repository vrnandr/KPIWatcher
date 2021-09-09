package com.example.vrnandr.kpiwatcher.utility

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vrnandr.kpiwatcher.MainActivity
import com.example.vrnandr.kpiwatcher.NOTIFICATION_CHANNEL_KPI_CHANGE
import com.example.vrnandr.kpiwatcher.R

fun notify(context: Context, kpiString:String){
    val listKpi = convertKPI(kpiString)
    var notificationText = ""
    for (kpi in listKpi){
        val kpiFloat = kpi.value.toFloatOrNull()
        // в нотификации первая запись и не равные 100
        if ((kpiFloat!=null && kpiFloat!=100f) || kpi==listKpi.first())
            notificationText+="${kpi.value} ${kpi.text}\n"
    }
    notificationText.dropLast(2)
    val colorString = listKpi.first().color
    var notificationIconColor = Color.GREEN
    when (colorString) {
        "orange" -> notificationIconColor = Color.parseColor("#FFA500")
        "red" -> notificationIconColor = Color.RED
    }
    val idIcon = idIconForNotification(listKpi.first().value.toFloatOrNull()?:0f)

    val intent = Intent(context,MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent : PendingIntent = PendingIntent.getActivity(context,0,intent,0)

    val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_KPI_CHANGE)
            .setSmallIcon(idIcon)
            .setContentTitle(context.resources.getString(R.string.kpi_changed))
            .setContentText(notificationText)
            .setColor(notificationIconColor)
            .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(notificationText))
            .setContentIntent(pendingIntent)
            .build()
    NotificationManagerCompat.from(context).notify(0, notification)
}

    fun idIconForNotification(value:Float):Int{
        return when (value.toInt()){
            0 -> R.drawable.ic_circle
            1 -> R.drawable.ic_1
            2 -> R.drawable.ic_2
            3 -> R.drawable.ic_3
            4 -> R.drawable.ic_4
            5 -> R.drawable.ic_5
            6 -> R.drawable.ic_6
            7 -> R.drawable.ic_7
            8 -> R.drawable.ic_8
            9 -> R.drawable.ic_9
            10 -> R.drawable.ic_10
            11 -> R.drawable.ic_11
            12 -> R.drawable.ic_12
            13 -> R.drawable.ic_13
            14 -> R.drawable.ic_14
            15 -> R.drawable.ic_15
            16 -> R.drawable.ic_16
            17 -> R.drawable.ic_17
            18 -> R.drawable.ic_18
            19 -> R.drawable.ic_19
            20 -> R.drawable.ic_20
            21 -> R.drawable.ic_21
            22 -> R.drawable.ic_22
            23 -> R.drawable.ic_23
            24 -> R.drawable.ic_24
            25 -> R.drawable.ic_25
            26 -> R.drawable.ic_26
            27 -> R.drawable.ic_27
            28 -> R.drawable.ic_28
            29 -> R.drawable.ic_29
            30 -> R.drawable.ic_30
            31 -> R.drawable.ic_31
            32 -> R.drawable.ic_32
            33 -> R.drawable.ic_33
            34 -> R.drawable.ic_34
            35 -> R.drawable.ic_35
            36 -> R.drawable.ic_36
            37 -> R.drawable.ic_37
            38 -> R.drawable.ic_38
            39 -> R.drawable.ic_39
            40 -> R.drawable.ic_40
            41 -> R.drawable.ic_41
            42 -> R.drawable.ic_42
            43 -> R.drawable.ic_43
            44 -> R.drawable.ic_44
            45 -> R.drawable.ic_45
            46 -> R.drawable.ic_46
            47 -> R.drawable.ic_47
            48 -> R.drawable.ic_48
            49 -> R.drawable.ic_49
            50 -> R.drawable.ic_50
            51 -> R.drawable.ic_51
            52 -> R.drawable.ic_52
            53 -> R.drawable.ic_53
            54 -> R.drawable.ic_54
            55 -> R.drawable.ic_55
            56 -> R.drawable.ic_56
            57 -> R.drawable.ic_57
            58 -> R.drawable.ic_58
            59 -> R.drawable.ic_59
            60 -> R.drawable.ic_60
            61 -> R.drawable.ic_61
            62 -> R.drawable.ic_62
            63 -> R.drawable.ic_63
            64 -> R.drawable.ic_64
            65 -> R.drawable.ic_65
            66 -> R.drawable.ic_66
            67 -> R.drawable.ic_67
            68 -> R.drawable.ic_68
            69 -> R.drawable.ic_69
            70 -> R.drawable.ic_70
            71 -> R.drawable.ic_71
            72 -> R.drawable.ic_72
            73 -> R.drawable.ic_73
            74 -> R.drawable.ic_74
            75 -> R.drawable.ic_75
            76 -> R.drawable.ic_76
            77 -> R.drawable.ic_77
            78 -> R.drawable.ic_78
            79 -> R.drawable.ic_79
            80 -> R.drawable.ic_80
            81 -> R.drawable.ic_81
            82 -> R.drawable.ic_82
            83 -> R.drawable.ic_83
            84 -> R.drawable.ic_84
            85 -> R.drawable.ic_85
            86 -> R.drawable.ic_86
            87 -> R.drawable.ic_87
            88 -> R.drawable.ic_88
            89 -> R.drawable.ic_89
            90 -> R.drawable.ic_90
            91 -> R.drawable.ic_91
            92 -> R.drawable.ic_92
            93 -> R.drawable.ic_93
            94 -> R.drawable.ic_94
            95 -> R.drawable.ic_95
            96 -> R.drawable.ic_96
            97 -> R.drawable.ic_97
            98 -> R.drawable.ic_98
            99 -> R.drawable.ic_99
            100 -> R.drawable.ic_100
            else -> R.drawable.ic_circle
        }
    }

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
        return when (value){
            0f -> R.drawable.ic_circle
            1f -> R.drawable.ic_1
            2f -> R.drawable.ic_2
            3f -> R.drawable.ic_3
            4f -> R.drawable.ic_4
            5f -> R.drawable.ic_5
            6f -> R.drawable.ic_6
            7f -> R.drawable.ic_7
            8f -> R.drawable.ic_8
            9f -> R.drawable.ic_9
            10f -> R.drawable.ic_10
            11f -> R.drawable.ic_11
            12f -> R.drawable.ic_12
            13f -> R.drawable.ic_13
            14f -> R.drawable.ic_14
            15f -> R.drawable.ic_15
            16f -> R.drawable.ic_16
            17f -> R.drawable.ic_17
            18f -> R.drawable.ic_18
            19f -> R.drawable.ic_19
            20f -> R.drawable.ic_20
            21f -> R.drawable.ic_21
            22f -> R.drawable.ic_22
            23f -> R.drawable.ic_23
            24f -> R.drawable.ic_24
            25f -> R.drawable.ic_25
            26f -> R.drawable.ic_26
            27f -> R.drawable.ic_27
            28f -> R.drawable.ic_28
            29f -> R.drawable.ic_29
            30f -> R.drawable.ic_30
            31f -> R.drawable.ic_31
            32f -> R.drawable.ic_32
            33f -> R.drawable.ic_33
            34f -> R.drawable.ic_34
            35f -> R.drawable.ic_35
            36f -> R.drawable.ic_36
            37f -> R.drawable.ic_37
            38f -> R.drawable.ic_38
            39f -> R.drawable.ic_39
            40f -> R.drawable.ic_40
            41f -> R.drawable.ic_41
            42f -> R.drawable.ic_42
            43f -> R.drawable.ic_43
            44f -> R.drawable.ic_44
            45f -> R.drawable.ic_45
            46f -> R.drawable.ic_46
            47f -> R.drawable.ic_47
            48f -> R.drawable.ic_48
            49f -> R.drawable.ic_49
            50f -> R.drawable.ic_50
            51f -> R.drawable.ic_51
            52f -> R.drawable.ic_52
            53f -> R.drawable.ic_53
            54f -> R.drawable.ic_54
            55f -> R.drawable.ic_55
            56f -> R.drawable.ic_56
            57f -> R.drawable.ic_57
            58f -> R.drawable.ic_58
            59f -> R.drawable.ic_59
            60f -> R.drawable.ic_60
            61f -> R.drawable.ic_61
            62f -> R.drawable.ic_62
            63f -> R.drawable.ic_63
            64f -> R.drawable.ic_64
            65f -> R.drawable.ic_65
            66f -> R.drawable.ic_66
            67f -> R.drawable.ic_67
            68f -> R.drawable.ic_68
            69f -> R.drawable.ic_69
            70f -> R.drawable.ic_70
            71f -> R.drawable.ic_71
            72f -> R.drawable.ic_72
            73f -> R.drawable.ic_73
            74f -> R.drawable.ic_74
            75f -> R.drawable.ic_75
            76f -> R.drawable.ic_76
            77f -> R.drawable.ic_77
            78f -> R.drawable.ic_78
            79f -> R.drawable.ic_79
            80f -> R.drawable.ic_80
            81f -> R.drawable.ic_81
            82f -> R.drawable.ic_82
            83f -> R.drawable.ic_83
            84f -> R.drawable.ic_84
            85f -> R.drawable.ic_85
            86f -> R.drawable.ic_86
            87f -> R.drawable.ic_87
            88f -> R.drawable.ic_88
            89f -> R.drawable.ic_89
            90f -> R.drawable.ic_90
            91f -> R.drawable.ic_91
            92f -> R.drawable.ic_92
            93f -> R.drawable.ic_93
            94f -> R.drawable.ic_94
            95f -> R.drawable.ic_95
            96f -> R.drawable.ic_96
            97f -> R.drawable.ic_97
            98f -> R.drawable.ic_98
            99f -> R.drawable.ic_99
            100f -> R.drawable.ic_100
            else -> R.drawable.ic_circle
        }
    }

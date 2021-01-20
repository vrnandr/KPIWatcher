package com.example.vrnandr.kpiwatcher.logger

import android.os.Environment
import android.util.Log
import fr.bipi.tressence.file.FileLoggerTree
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val LOG_DIR = "KPIWatcher"
//const val LOG_DIR = "OSKMobile"

class MyFileLoggerTree {

    private val sdcard = Environment.getExternalStorageDirectory().absolutePath
    private val logDir = File("$sdcard/$LOG_DIR")
    init {
        if (!logDir.exists())
            logDir.mkdirs()
    }
    private val logFileName = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) + "KPIWatcher.log"
    private val fileLoggerTree = FileLoggerTree.Builder()
            .withFileName(logFileName)
            .withDir(logDir)
            .withSizeLimit(10000)
            .withMinPriority(Log.DEBUG)
            .appendToFile(true)
            .build()



    fun get():FileLoggerTree{
        return fileLoggerTree
    }
}
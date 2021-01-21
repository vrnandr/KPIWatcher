package com.example.vrnandr.kpiwatcher.logger

import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


private const val LOG_DIR = "KPIWatcher"
//const val LOG_DIR = "OSKMobile"

class MyFileLoggerTree: Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val logTimeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()).format(Date())
            val logFileName = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) + "KPIWatcher.log"
            val file = generateFile(logFileName)
            if (file != null) {
                val writer = FileWriter(file, true)
                writer.append("$logTimeStamp - $tag: $message\r\n")
                writer.flush()
                writer.close()
            }
        } catch (e: Exception) {
            Timber.e("Error while logging into file : $e")
        }
    }

    private fun generateFile(fileName: String): File? {
        var file: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {

            val sdcard = Environment.getExternalStorageDirectory().absolutePath
            val logDir = File("$sdcard/$LOG_DIR")

            var dirExists = true
            if (!logDir.exists()) {
                dirExists = logDir.mkdirs()
            }
            if (dirExists) {
                file = File(logDir, fileName)
            }
        }
        return file
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }
}
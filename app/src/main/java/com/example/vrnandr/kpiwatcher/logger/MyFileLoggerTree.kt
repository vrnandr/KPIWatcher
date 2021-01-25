package com.example.vrnandr.kpiwatcher.logger

import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


private const val LOG_DIR = "KPIWatcher"
private const val MAX_COUNT_LOG_FILES=5
private const val LOG_SUFFIX = "KPIWatcher.log"

class MyFileLoggerTree: Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val logTimeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()).format(Date())
            val logFileName = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) + LOG_SUFFIX
            val file = generateFile(logFileName)
            cleanupLogDir()
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

    private fun cleanupLogDir(){
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val sdcard = Environment.getExternalStorageDirectory().absolutePath
            val dir = File("$sdcard/$LOG_DIR").listFiles()
            dir?.let {
                dir.sortBy { truncAndInvertDate(it.name) }
                dir.reverse()
                for (i in dir.indices){
                    if (i >= MAX_COUNT_LOG_FILES){
                        if (dir[i].delete())
                            Timber.d("${dir[i].name} deleted")
                    }

                }
            }
        }
    }

    private fun truncAndInvertDate(name:String):String{
        val date = name.substringBefore(LOG_SUFFIX)
        val day = date.substringBefore('-')
        val month =  date.substringAfter('-').substringBefore('-')
        val year = date.substringAfterLast('-')
        return "$year-$month-$day"
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }
}
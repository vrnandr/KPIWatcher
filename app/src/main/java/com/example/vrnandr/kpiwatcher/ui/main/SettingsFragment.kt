package com.example.vrnandr.kpiwatcher.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.preference.*
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.WORKER_TAG
import com.example.vrnandr.kpiwatcher.repository.DEFAULT_TIMER_LONG
import com.example.vrnandr.kpiwatcher.repository.MIN_TIMER_LONG
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val ID_READ_PERMISSION = 1
private const val ID_WRITE_PERMISSION = 2

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var timer: EditTextPreference
    private lateinit var refreshMethod: ListPreference
    private lateinit var enableLogging: SwitchPreferenceCompat
    private lateinit var startRefreshMethod:String
    private lateinit var startTimer:String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        timer = findPreference("timer")!!
        refreshMethod = findPreference("refresh_method")!!
        enableLogging = findPreference("enable_logging")!!

        startRefreshMethod = refreshMethod.value
        startTimer = timer.text

        timer.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        timer.isEnabled = refreshMethod.value != "off"

        refreshMethod.setOnPreferenceChangeListener { _, newValue ->
            when (newValue){
                "off" -> {
                    timer.isEnabled = false
                }
                "log_file" -> {
                    timer.isEnabled = true
                    timer.text = getString(R.string.min_timer)
                    checkReadPermission()
                }
                "periodic" -> {
                    timer.isEnabled = true
                    timer.text = getString(R.string.periodic_timer)
                }
            }
            true
        }

        enableLogging.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean){
                checkWritePermission()
            }
            true
        }
        //проверить и выставить значения настроек в зависимости от runtime permissions
        checkPermission()

    }

    override fun onStop() {
        super.onStop()
        if (startRefreshMethod!=refreshMethod.value || startTimer!=timer.text){
            var timerLong = timer.text?.toLongOrNull()?: DEFAULT_TIMER_LONG
            if (timerLong < 15) timerLong = MIN_TIMER_LONG
            //Timber.d("timer is $timer, refresh method is ${refreshMethod?.value}")
            when (refreshMethod.value){
                "off" -> {
                    activity?.let {
                        WorkManager.getInstance(it).cancelAllWork()
                    }
                    Timber.d("worker off")
                }
                "log_file", "periodic" -> {
                    activity?.let {
                        val updateWorker = PeriodicWorkRequestBuilder<UpdateWorker>(timerLong, TimeUnit.MINUTES).build()
                        WorkManager.getInstance(it)
                                .enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, updateWorker)
                    }
                    Timber.d("worker on $timerLong minutes")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.settingHelp -> {
                showHelp()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showHelp(){
        val helpDialog = HelpDialogFragment()
        helpDialog.show(childFragmentManager, "help_dialog")
    }

    private fun checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){
                if (refreshMethod.value=="log_file")
                    refreshMethod.value = "periodic"
            }
            if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){
                enableLogging.isChecked = false
            }
        }
    }

    private fun checkReadPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED)
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), ID_READ_PERMISSION)

    }
    private fun checkWritePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED)
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), ID_WRITE_PERMISSION)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == ID_READ_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
            refreshMethod.value = "periodic"
        }
        if (requestCode == ID_WRITE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
            enableLogging.isChecked = false
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
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
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val ID_READ_PERMISSION = 1
private const val ID_WRITE_PERMISSION = 2

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setHasOptionsMenu(true)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val timer = findPreference<EditTextPreference>("timer")
        timer?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            setOnPreferenceChangeListener { _, newValue ->
                val intValue = newValue.toString().toIntOrNull()
                if (intValue!=null && intValue<15)
                    timer.text = getString(R.string.min_timer)
                true
            }
        }

        val refreshMethod = findPreference<ListPreference>("refresh_method")
        timer?.isEnabled = refreshMethod?.value != "off"
        refreshMethod?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue){
                "off" -> {
                    timer?.isEnabled = false
                }
                "log_file" -> {
                    timer?.isEnabled = true
                    timer?.text = getString(R.string.min_timer)
                    checkReadPermission()
                }
                "periodic" -> {
                    timer?.isEnabled = true
                    timer?.text = getString(R.string.periodic_timer)
                }
            }
            //timer?.isEnabled = newValue != "off"
            true
        }

        val enableLogging = findPreference<SwitchPreferenceCompat>("enable_logging")
        enableLogging?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean){
                checkWritePermission()
            }
            true
        }
        //проверить и выставить значения настроек в зависимости от runtime permissions
        checkPermission()

        /*if (refreshMethod?.callChangeListener("log_file") == true)
            Toast.makeText(activity,list.value,Toast.LENGTH_SHORT).show()*/

    }


    // FIXME: 24.02.2021 воркеры перезапускаются при каждом закрытии окна настроек
    override fun onStop() {
        super.onStop()
        val timer = findPreference<EditTextPreference>("timer")?.text?.toLongOrNull()?: DEFAULT_TIMER_LONG
        val refreshMethod = findPreference<ListPreference>("refresh_method")
        //Timber.d("timer is $timer, refresh method is ${refreshMethod?.value}")
        when (refreshMethod?.value){
            "off" -> {
                activity?.let {
                    WorkManager.getInstance(it).cancelAllWork()
                }
                Timber.d("worker off")
            }
            "log_file", "periodic" -> {
                activity?.let {
                    val updateWorker = PeriodicWorkRequestBuilder<UpdateWorker>(timer, TimeUnit.MINUTES).build()
                    WorkManager.getInstance(it)
                            .enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, updateWorker)
                }
                Timber.d("worker on $timer minutes")
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        /*val key = preference?.key
        if (key == "refresh_method"){
            val value = (preference as ListPreference).value
            findPreference<EditTextPreference>("timer")?.isEnabled = value != "off"
        }*/
        return super.onPreferenceTreeClick(preference)
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
                val refreshMethod = findPreference<ListPreference>("refresh_method")
                if (refreshMethod?.value=="log_file")
                    refreshMethod.value = "periodic"
            }
            if (activity?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){
                val enableLogging = findPreference<SwitchPreferenceCompat>("enable_logging")
                enableLogging?.isChecked = false
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
            val refreshMethod = findPreference<ListPreference>("refresh_method")
            refreshMethod?.value = "periodic"
        }
        if (requestCode == ID_WRITE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
            val enableLogging = findPreference<SwitchPreferenceCompat>("enable_logging")
            enableLogging?.isChecked = false
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
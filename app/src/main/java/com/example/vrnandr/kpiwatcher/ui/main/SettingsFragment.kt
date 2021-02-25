package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Bundle
import android.text.InputType
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

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
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
                }
                "periodic" -> {
                    timer?.isEnabled = true
                    timer?.text = getString(R.string.periodic_timer)
                }
            }
            //timer?.isEnabled = newValue != "off"
            true
        }
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

}
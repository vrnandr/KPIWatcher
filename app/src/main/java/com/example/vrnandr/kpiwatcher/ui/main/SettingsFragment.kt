package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Bundle
import android.text.InputType
import androidx.preference.*
import com.example.vrnandr.kpiwatcher.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //preferenceManager.sharedPreferencesName = "settings11"
       // preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
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

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        /*val key = preference?.key
        if (key == "refresh_method"){
            val value = (preference as ListPreference).value
            findPreference<EditTextPreference>("timer")?.isEnabled = value != "off"
        }*/
        return super.onPreferenceTreeClick(preference)
    }

}
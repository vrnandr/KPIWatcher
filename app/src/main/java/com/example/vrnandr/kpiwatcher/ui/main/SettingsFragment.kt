package com.example.vrnandr.kpiwatcher.ui.main

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.preference.*
import com.example.vrnandr.kpiwatcher.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //preferenceManager.sharedPreferencesName = "settings11"
       // preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val timer = findPreference<EditTextPreference>("timer")
        timer?.setOnPreferenceChangeListener { preference, newValue ->
            val intTimer = newValue.toString().toIntOrNull()
            if (intTimer!=null && intTimer < 15){
                (preference as EditTextPreference).text = getString(R.string.min_timer)
            }
            true
        }
        timer?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            /*val intTimer = editText.text.toString().toIntOrNull()
            if (intTimer!=null&&intTimer<15){
                editText.setText(R.string.min_timer)
            }*/
        }

        timer?.callChangeListener("15")


    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key
        Toast.makeText(activity,key,Toast.LENGTH_SHORT).show()
        return super.onPreferenceTreeClick(preference)
    }

    override fun onResume() {
        super.onResume()
        //PreferenceManager.setDefaultValues(activity,R.xml.root_preferences,false)
        /*val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val useLog = sharedPreferences.getBoolean("enable_logging", false)
        val enableLogging = findPreference<SwitchPreferenceCompat>("enable_logging")
        enableLogging?.isChecked = useLog*/

    }
}
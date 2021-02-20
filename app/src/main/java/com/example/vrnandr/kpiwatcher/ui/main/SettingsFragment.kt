package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.vrnandr.kpiwatcher.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = "settings"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
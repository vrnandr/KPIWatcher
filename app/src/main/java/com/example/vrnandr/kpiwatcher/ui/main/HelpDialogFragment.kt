package com.example.vrnandr.kpiwatcher.ui.main

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.vrnandr.kpiwatcher.R

class HelpDialogFragment:DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.help_message)
                    .setPositiveButton(R.string.ok_button,null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
package com.example.vrnandr.kpiwatcher.ui.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.vrnandr.kpiwatcher.R
import java.lang.ClassCastException

class DetailSettingsFragment:DialogFragment() {

    private lateinit var listener: DetailSettingsListener

    interface DetailSettingsListener {
        fun onItemCLick(item: String, isChecked: Boolean)
        fun onOkClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as DetailSettingsListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement DetailSettingsListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val titles = requireArguments().getStringArray(ALL_KPI_LIST)//?.joinToString(separator = "\n"){s -> s.toString()}
            val visibleKPI = requireArguments().getString(VISIBLE_KPI_LIST)

            val listVisibleKPI = arrayListOf<Boolean>()
            if (titles != null && visibleKPI != null)
                for (title in titles)
                    listVisibleKPI.add(visibleKPI.contains(title))

            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.detail_fragment_settings_title)
                //.setMessage(titles)
                    .setMultiChoiceItems(titles, listVisibleKPI.toBooleanArray()) { _, which, isChecked ->
                        titles?.get(which)?.let { item -> listener.onItemCLick(item,isChecked) }
                    }
                    .setPositiveButton(R.string.ok_button) { _, _ ->
                        listener.onOkClick()
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
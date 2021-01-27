package com.example.vrnandr.kpiwatcher.ui.main

import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi

@Suppress("UNUSED_PARAMETER")
class MainViewModel: ViewModel() {
    private val repo = Repository.get()
    val responseKPE = repo.responseKPE
    //val showErrorToast = repo.showErrorToast

    val useLogFile = repo.getUseLogFile()

    val currentKpi : LiveData<Kpi> = repo.liveDataCurrentKPI

    fun onKPIButtonClick (view : View){
       repo.kpiRequest()
    }

    private val _time = MutableLiveData<String>()
    val time: LiveData<String>
        get() = _time

    private val _messageToShow = MutableLiveData<String>()
    val messageToShow: LiveData<String>
        get() = _messageToShow

    fun onStopWorkerClick (view: View){
        _time.postValue(System.currentTimeMillis().toString())
    }

    fun onClick (view: View){
        when(view.id){
            R.id.useLogFile -> {
                repo.setUseLogFile((view as CheckBox).isChecked)
            }
        }
    }
}
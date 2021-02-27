package com.example.vrnandr.kpiwatcher.ui.main

import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.WORKER_TAG
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import java.util.concurrent.TimeUnit

@Suppress("UNUSED_PARAMETER")
class MainViewModel: ViewModel() {
    private val repo = Repository.get()
    val responseKPE = repo.responseKPE
    val showToast = repo.showToast

    val currentKpi : LiveData<Kpi> = repo.liveDataCurrentKPI

    fun onKPIButtonClick (){
       repo.kpiRequest()
    }
}
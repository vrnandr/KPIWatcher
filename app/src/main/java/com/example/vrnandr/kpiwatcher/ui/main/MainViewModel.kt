package com.example.vrnandr.kpiwatcher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi

@Suppress("UNUSED_PARAMETER")
class MainViewModel: ViewModel() {
    private val repo = Repository.get()
    val responseKPE = repo.responseKPE
    val showToastEvent = repo.showToastEvent
    val showErrorToastEvent = repo.showErrorToastEvent

    val currentKpi : LiveData<Kpi?> = repo.currentKPI
    val successKPIRequestEvent = repo.successKPIRequestEvent

    init {
        repo.getCurrentKPI()
    }

    fun onKPIButtonClick (){
       repo.kpiRequest()
    }

    fun deleteCredentials(){
        repo.deleteCredentials()
    }
}
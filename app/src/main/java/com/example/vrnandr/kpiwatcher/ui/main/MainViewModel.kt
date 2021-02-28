package com.example.vrnandr.kpiwatcher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi

@Suppress("UNUSED_PARAMETER")
class MainViewModel: ViewModel() {
    private val repo = Repository.get()
    val responseKPE = repo.responseKPE
    val showToast = repo.showToast

    val currentKpi : LiveData<Kpi> = repo.liveDataCurrentKPI

    fun onKPIButtonClick (){
       repo.kpiRequest()
    }

    fun deleteCredentials(){
        repo.deleteCredentials()
    }
}
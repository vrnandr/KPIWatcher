package com.example.vrnandr.kpiwatcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.repository.database.Kpi
import kotlinx.coroutines.launch

class DetailViewModel :ViewModel() {
    private val repo = Repository.get()
    private val data = repo.allKpi
}
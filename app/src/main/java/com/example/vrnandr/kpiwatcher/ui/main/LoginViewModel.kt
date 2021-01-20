package com.example.vrnandr.kpiwatcher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository

class LoginViewModel : ViewModel() {
    private val repo = Repository.get()
    val successLogin = repo.successLogin

    fun login(login: String, password: String){
        repo.openLoginPage(login,password)
    }
}
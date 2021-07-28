package com.example.vrnandr.kpiwatcher.ui.main

import androidx.lifecycle.ViewModel
import com.example.vrnandr.kpiwatcher.repository.Repository

class LoginViewModel : ViewModel() {
    private val repo = Repository.get()
    val successLoginEvent = repo.successLoginEvent

    fun login(login: String, password: String){
        repo.openLoginPage(login,password)
    }
}
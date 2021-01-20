package com.example.vrnandr.kpiwatcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.ui.main.DetailFragment
import com.example.vrnandr.kpiwatcher.ui.main.LoginFragment
import com.example.vrnandr.kpiwatcher.ui.main.MainFragment

class MainActivity : AppCompatActivity(), MainFragment.Callbacks, LoginFragment.Callbacks {

    private val repo = Repository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val login = repo.getLogin()
        if (savedInstanceState == null) {
            if (login!=null){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow()
            }

        }
    }

    override fun showDetail() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance())
                .addToBackStack(null)
                .commitNow()
    }

    override fun logout() {
        //repo.exitPressed()
        repo.deleteCredentials()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
    }

    override fun onLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }


}
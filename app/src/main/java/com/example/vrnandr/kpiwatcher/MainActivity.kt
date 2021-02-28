package com.example.vrnandr.kpiwatcher

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.worker.UpdateWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val repo = Repository.get()
    private val showErrorToast = repo.showErrorToast
    private val showToast = repo.showToast

    private lateinit var navController: NavController

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        showErrorToast.observe(this,{ showToast(it) })
        showToast.observe(this,{ showToast(it) })

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.detailFragment, R.id.mainFragment, R.id.settingsFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val login = repo.getLogin()
        if (savedInstanceState == null) {
            if (login == null){
                navController.navigate(R.id.action_mainFragment_to_loginFragment)
            } else if (repo.useWorker()) {
                val updateWorker = PeriodicWorkRequestBuilder<UpdateWorker>(repo.getTimer(), TimeUnit.MINUTES).build()
                WorkManager.getInstance(this).apply {
                    //cancelAllWork()
                    enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, updateWorker)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.loginFragment) {
                navView.visibility = View.GONE
                supportActionBar?.setShowHideAnimationEnabled(false)
                supportActionBar?.hide()
            } else {
                navView.visibility = View.VISIBLE
                supportActionBar?.setShowHideAnimationEnabled(true)
                supportActionBar?.show()
            }
        }
    }

    private fun showToast(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

}
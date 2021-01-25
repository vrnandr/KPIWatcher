package com.example.vrnandr.kpiwatcher

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.example.vrnandr.kpiwatcher.repository.Repository
import com.example.vrnandr.kpiwatcher.ui.main.DetailFragment
import com.example.vrnandr.kpiwatcher.ui.main.LoginFragment
import com.example.vrnandr.kpiwatcher.ui.main.MainFragment
import timber.log.Timber
import java.util.jar.Manifest

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
        checkPermission()
    }

    private fun checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //TODO разделить чтение/запись
        if (!(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED)){
            WorkManager.getInstance(this).cancelAllWork()
            finish()
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun showDetail() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    override fun logout() {
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
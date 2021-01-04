package com.example.vrnandr.kpiwatcher.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vrnandr.kpiwatcher.repository.Repository

class UpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var repo :Repository = Repository.get()
    override fun doWork(): Result {
        repo.kpiRequest()
        Log.d("my", "doWork: run work")
        return Result.success()
    }
}
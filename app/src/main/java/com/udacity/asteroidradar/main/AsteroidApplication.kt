package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import com.udacity.asteroidradar.work.AsteroidDataWorker
import com.udacity.database.AsteroidDatabase
import com.udacity.database.AsteroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

// This class is used to encapsulate the database and the repository instantiation for only when they are needed over when the app starts everytime.
class AsteroidApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    val asteroidDatabase by lazy { AsteroidDatabase.getInstance(this) }
    val repository by lazy { AsteroidRepository(asteroidDatabase) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<AsteroidDataWorker>(
            1,
            TimeUnit.DAYS
        ).setConstraints(constraints)
         .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            AsteroidDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
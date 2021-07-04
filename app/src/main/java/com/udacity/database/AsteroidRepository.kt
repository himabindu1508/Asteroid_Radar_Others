package com.udacity.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Database
import com.google.gson.JsonObject
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.network.NasaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.util.*

class AsteroidRepository(private val asteroidDatabase: AsteroidDatabase) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _startDate = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    private val _endDate = _startDate.plusDays(7)

    @RequiresApi(Build.VERSION_CODES.O)
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(asteroidDatabase.asteroidDao.getAsteroids()) {
         it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayAsteroids: LiveData<List<Asteroid>> = Transformations.map(asteroidDatabase.asteroidDao.getTodayAsteroids(_startDate.toString())) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val weeklyAsteroids: LiveData<List<Asteroid>> = Transformations.map(asteroidDatabase.asteroidDao.getWeeklyAsteroids(_startDate.toString(), _endDate.toString())) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val jsonResult = NasaApi.retrofitService.getAsteroids()
            val asteroids = parseAsteroidsJsonResult(JSONObject(jsonResult))
            val listDbAsteroids = mutableListOf<DatabaseAsteroid>()
            print(asteroids)

            for (asteroid in asteroids) {
                val databaseAsteroid = DatabaseAsteroid(asteroid.id,
                                                        asteroid.codename,
                                                        asteroid.closeApproachDate,
                                                        asteroid.absoluteMagnitude,
                                                        asteroid.estimatedDiameter,
                                                        asteroid.relativeVelocity,
                                                        asteroid.distanceFromEarth,
                                                        asteroid.isPotentiallyHazardous
                )

                listDbAsteroids.add(databaseAsteroid)
            }

            asteroidDatabase.asteroidDao.insertAll(listDbAsteroids.toList())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            val pictureOfDay = NasaApi.retrofitService.getImageOfTheDay(LocalDate.now())
            val dbPictureOfDay = DatabasePictureOfDay(pictureOfDay.url, pictureOfDay.title, pictureOfDay.mediaType)
            asteroidDatabase.asteroidDao.insertPictureOfTheDay(dbPictureOfDay)
        }
    }

    @WorkerThread
    suspend fun getAsteroidImageOfTheDay(): PictureOfDay {
        val databasePictureOfDay = asteroidDatabase.asteroidDao.getPictureOfTheDay()
        return PictureOfDay(databasePictureOfDay.mediaType, databasePictureOfDay.title, databasePictureOfDay.url)
    }

}

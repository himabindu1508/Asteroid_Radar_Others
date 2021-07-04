package com.udacity.database

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid

@Entity(tableName = "asteroids")
data class DatabaseAsteroid constructor(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,
        @ColumnInfo(name="codename")
        var codename: String,
        @ColumnInfo(name = "close_approach_date")
        var closeApproachDate: String,
        @ColumnInfo(name = "absolute_magnitude")
        val absoluteMagnitude: Double,
        @ColumnInfo(name = "estimated_diameter")
        val estimatedDiameter: Double,
        @ColumnInfo(name = "relative_velocity")
        val relativeVelocity: Double,
        @ColumnInfo(name = "distance_from_earth")
        val distanceFromEarth: Double,
        @ColumnInfo(name = "is_asteroid_hazardous")
        val isPotentiallyHazardous: Boolean
)


// Extension function that converts from database objects to domain objects.
fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
        return map {
            Asteroid(
                    id = it.id,
                    codename = it.codename,
                    closeApproachDate = it.closeApproachDate,
                    absoluteMagnitude = it.absoluteMagnitude,
                    estimatedDiameter = it.estimatedDiameter,
                    relativeVelocity = it.relativeVelocity,
                    distanceFromEarth = it.distanceFromEarth,
                    isPotentiallyHazardous = it.isPotentiallyHazardous
            )
        }
}
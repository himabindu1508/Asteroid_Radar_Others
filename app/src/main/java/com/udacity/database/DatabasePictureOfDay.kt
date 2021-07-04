package com.udacity.database

import androidx.lifecycle.Transformations.map
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "picture_of_day")
data class DatabasePictureOfDay constructor(
        @PrimaryKey
        var url: String,
        @ColumnInfo(name = "title")
        var title: String,
        @ColumnInfo(name = "media_type")
        var mediaType: String
)
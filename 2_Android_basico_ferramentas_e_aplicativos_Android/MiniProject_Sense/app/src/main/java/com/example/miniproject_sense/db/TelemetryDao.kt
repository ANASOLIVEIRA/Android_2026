package com.example.miniproject_sense.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TelemetryDao {

    @Insert
    suspend fun insert(item: TelemetryEntity)

    @Query("SELECT * FROM telemetry ORDER BY ts DESC LIMIT :limit")
    suspend fun last(limit: Int): List<TelemetryEntity>
}
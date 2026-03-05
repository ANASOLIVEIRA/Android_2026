package com.example.miniproject_sense.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "telemetry")
data class TelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ts: Long,
    val temperature: Double,
    val humidity: Double,
    val motionValue: Double
)
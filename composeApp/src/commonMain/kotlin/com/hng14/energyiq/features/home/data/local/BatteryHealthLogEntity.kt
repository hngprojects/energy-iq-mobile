package com.hng14.energyiq.features.home.data.local

import androidx.room3.Entity

@Entity(
    tableName = "battery_health_logs",
    primaryKeys = ["userId", "inverterId", "recordedAt"],
)
data class BatteryHealthLogEntity(
    val userId: String,
    val inverterId: String,
    val recordedAt: String, // ISO string from backend, used for ordering + de-dupe
    val status: String,
    val reason: String,
    val dataAgeSeconds: Int?,
    val systemOffline: Boolean,
)


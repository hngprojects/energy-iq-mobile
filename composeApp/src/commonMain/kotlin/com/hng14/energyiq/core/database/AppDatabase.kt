package com.hng14.energyiq.core.database

import androidx.room3.ConstructedBy
import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.hng14.energyiq.features.auth.data.local.UserDao
import com.hng14.energyiq.features.auth.data.local.UserEntity
import com.hng14.energyiq.features.home.data.local.BatteryHealthLogDao
import com.hng14.energyiq.features.home.data.local.BatteryHealthLogEntity

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Database(
    entities = [UserEntity::class, BatteryHealthLogEntity::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ],
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun batteryHealthLogDao(): BatteryHealthLogDao
}

const val APP_DATABASE_NAME = "kotlin_starter.db"

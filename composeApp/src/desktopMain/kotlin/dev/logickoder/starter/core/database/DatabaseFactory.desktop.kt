package dev.logickoder.starter.core.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual fun createDatabase(context: Any?): AppDatabase {
    val appDir = File(System.getProperty("user.home"), ".kotlin_starter").also { it.mkdirs() }
    return Room.databaseBuilder<AppDatabase>(
        File(appDir, APP_DATABASE_NAME).absolutePath,
    ).setDriver(driver = BundledSQLiteDriver()).build()
}

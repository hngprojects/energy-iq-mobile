package dev.logickoder.starter.core.database

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

actual fun createDatabase(context: Any?): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(name = APP_DATABASE_NAME)
        .setDriver(WebWorkerSQLiteDriver(createWorker()))
        .build()
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun createWorker(): Worker =
    js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url), { type: 'module' })""")

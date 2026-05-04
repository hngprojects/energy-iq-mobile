package dev.logickoder.starter.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual fun platformHttpEngine(): HttpClientEngine = Js.create()

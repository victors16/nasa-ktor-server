package com

import com.di.appModule
import com.plugins.configureRouting
import com.repositories.NasaRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.* // Server Plugin
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.cio.CIO
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(
        CIO,
        // PRO TIP: Si existe la variable PORT úsala, si no, usa 8080 (para local)
        port = System.getenv("PORT")?.toInt() ?: 8080,
        host = "0.0.0.0", // VITAL para Docker/Render
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    // Install Koin Plugin
    install(Koin) {
        slf4jLogger() // Log Koin events
        modules(appModule) // Load our definitions
    }

    // Initialize DB (We can retrieve the repo from Koin to init it)
    // This is how you manually get an instance from Koin container:
    val repo by inject<NasaRepository>()
    repo.init()

    configureSerialization()
    configureRouting()
}

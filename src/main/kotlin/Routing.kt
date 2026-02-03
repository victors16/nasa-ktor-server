package com

import com.models.ProcessedNasaResponse
import com.services.NasaService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    // Lazy injection: The service is only retrieved when needed
    val nasaService by inject<NasaService>()

    routing {
        get("/nasa/{date}") {
            val date = call.parameters["date"] ?: return@get

            // Usage
            val data = nasaService.fetchApod(date)

            call.respond(data)
        }
    }
}

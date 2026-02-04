package com

import com.services.NasaService
import com.templates.nasaPage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.* // Necesario para call.respondHtml
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate

fun Application.configureRouting() {
    val nasaService by inject<NasaService>()

    routing {
        // 1. RUTA WEB (Para el navegador) - RAIZ
        get("/") {
            val today = LocalDate.now().toString()
            try {
                val data = nasaService.fetchApod(today)
                // Aquí usamos respondHtml y llamamos a nuestra función del paso 2
                call.respondHtml(HttpStatusCode.OK) {
                    nasaPage(data)
                }
            } catch (e: Exception) {
                call.respondText("Error obteniendo la imagen: ${e.message}")
            }
        }

        // 2. RUTA API (Para tu futura App Móvil)
        // La movemos a un grupo "/api" para ser ordenados
        route("/api") {
            get("/nasa/{date}") {
                val date = call.parameters["date"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val data = nasaService.fetchApod(date)
                call.respond(data) // Esto devuelve JSON
            }

            // Un alias para 'today' en formato JSON
            get("/nasa/today") {
                val today = LocalDate.now().toString()
                val data = nasaService.fetchApod(today)
                call.respond(data)
            }
        }
    }
}
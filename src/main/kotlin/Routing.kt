package com.plugins

import com.services.NasaService
import com.templates.galleryPage
import com.templates.nasaPage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate

fun Application.configureRouting() {
    val nasaService by inject<NasaService>()

    routing {

        // 1. HOME: Foto del día o Fecha elegida
        get("/") {
            // Si viene el parámetro ?date=2023-01-01 lo usamos, si no, usamos hoy
            val dateParam = call.parameters["date"]
            val date = if (!dateParam.isNullOrBlank()) dateParam else LocalDate.now().toString()

            try {
                val data = nasaService.fetchApod(date)
                call.respondHtml { nasaPage(data) }
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}. Probablemente la fecha es futura o el formato incorrecto.")
            }
        }

        // 2. NUEVA VISTA: Galería (Solo el esqueleto HTML)
        get("/gallery") {
            call.respondHtml { galleryPage() }
        }

        // 3. API JSON: Usada por la App Móvil y ahora por el JAVASCRIPT de la Galería
        route("/api/nasa") {
            // Endpoint para una sola foto
            get("/{date}") {
                val date = call.parameters["date"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(nasaService.fetchApod(date))
            }

            // Endpoint para el LISTADO (Infinite Scroll)
            get("/list") {
                val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
                val limit = call.parameters["limit"]?.toIntOrNull() ?: 10

                try {
                    val list = nasaService.fetchRecentPhotos(offset, limit)
                    call.respond(list)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error")
                }
            }
        }
    }
}
package com

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Definimos el cliente como una variable global o dentro de un Object (Singleton)
val nasaHttpClient = HttpClient(CIO) {
    // 1. Instalamos el plugin para entender JSON
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            // VITAL: Si la NASA añade un campo nuevo mañana, esto evita que tu app explote
            ignoreUnknownKeys = true
        })
    }
}
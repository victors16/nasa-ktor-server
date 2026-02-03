package com.services

import com.models.ApodResponse
import com.repositories.NasaRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class NasaService(
    private val client: HttpClient, // Injected by Koin
    private val repository: NasaRepository // Injected by Koin
) {

    suspend fun fetchApod(date: String): ApodResponse {
        // 1. Try DB
        repository.findByDate(date)?.let { return it }

        // 2. Try API
        val response: ApodResponse = client.get("https://api.nasa.gov/planetary/apod") {
            url {
                parameters.append("api_key", "DEMO_KEY")
                parameters.append("date", date)
            }
        }.body()

        // 3. Save
        repository.save(response)

        return response
    }
}
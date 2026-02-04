package com.services

import com.models.ApodResponse
import com.repositories.NasaRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.time.LocalDate

class NasaService(
    private val client: HttpClient,
    private val repository: NasaRepository
) {

    suspend fun fetchApod(date: String): ApodResponse {
        repository.findByDate(date)?.let { return it }

        val response: ApodResponse = client.get("https://api.nasa.gov/planetary/apod") {
            url {
                parameters.append("api_key", System.getenv("NASA_API_KEY") ?: "DEMO_KEY")
                parameters.append("date", date)
            }
        }.body()

        repository.save(response)
        return response
    }

    suspend fun fetchRecentPhotos(offset: Int, limit: Int): List<ApodResponse> {
        val endDate = LocalDate.now().minusDays(offset.toLong())
        val startDate = endDate.minusDays(limit.toLong() - 1)

        val response: List<ApodResponse> = client.get("https://api.nasa.gov/planetary/apod") {
            url {
                parameters.append("api_key", System.getenv("NASA_API_KEY") ?: "DEMO_KEY")
                parameters.append("start_date", startDate.toString())
                parameters.append("end_date", endDate.toString())
            }
        }.body()

        response.forEach { repository.save(it) }

        return response.reversed()
    }
}
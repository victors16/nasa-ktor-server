package com.models

import kotlinx.serialization.Serializable

/**
 * Represents the processed response sent to our client.
 * This decouples the external NASA API format from our internal API format.
 */
@Serializable
data class ProcessedNasaResponse(
    val title: String,
    val url: String,
    val mediaType: String, // e.g., "image" or "video"
    val description: String,
    val processedAt: String = "Powered by Ktor 😎"
)
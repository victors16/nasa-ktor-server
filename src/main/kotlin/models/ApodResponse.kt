package com.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApodResponse(
    val title: String,
    // NASA sends "media_type" (snake_case), but we want "mediaType" (camelCase)
    // @SerialName acts as a bridge between the JSON field and our variable.
    @SerialName("media_type")
    val mediaType: String,
    val explanation: String,
    val url: String,
    val date: String,
    // La NASA a veces manda campos extra que no nos interesan.
    // Ktor ignorará los que no definamos aquí si configuramos bien el JSON luego.
)

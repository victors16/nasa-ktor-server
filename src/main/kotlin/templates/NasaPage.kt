package com.templates


import com.models.ApodResponse
import kotlinx.html.*

fun HTML.nasaPage(data: ApodResponse) {
    head {
        title { +"NASA APOD - ${data.date}" }
        // Un poco de CSS casero para que no se vea feo
        style {
            unsafe {
                raw("""
                    body { font-family: 'Segoe UI', sans-serif; background-color: #1a1a1a; color: #fff; margin: 0; padding: 20px; display: flex; flex-direction: column; align-items: center; }
                    .card { background-color: #2d2d2d; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.5); max-width: 800px; overflow: hidden; }
                    .image-container { width: 100%; }
                    img, iframe { width: 100%; display: block; }
                    .content { padding: 25px; }
                    h1 { margin-top: 0; color: #61dafb; }
                    .date { color: #888; font-size: 0.9em; margin-bottom: 15px; display: block; }
                    p { line-height: 1.6; }
                    .footer { margin-top: 20px; color: #555; font-size: 0.8em; }
                """.trimIndent())
            }
        }
    }
    body {
        div("card") {
            div("image-container") {
                if (data.mediaType == "image") {
                    img(src = data.url, alt = data.title)
                } else {
                    // Soporte para videos de YouTube que a veces manda la NASA
                    iframe {
                        src = data.url
                        width = "100%"
                        height = "400"
                        attributes["frameborder"] = "0"
                        attributes["allowfullscreen"] = "true"
                    }
                }
            }
            div("content") {
                h1 { +data.title }
                span("date") { +"📅 ${data.date}" }
                p { +data.explanation }
            }
        }
        div("footer") {
            +"Powered by Ktor & NASA Open API"
        }
    }
}
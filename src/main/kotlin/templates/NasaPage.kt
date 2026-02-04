package com.templates

import com.models.ApodResponse
import kotlinx.html.*

fun HTML.nasaPage(data: ApodResponse) {
    commonLayout("NASA APOD - ${data.date}") {

        // FORMULARIO DE FECHA
        form(action = "/", method = FormMethod.get, classes = "date-picker") {
            label { +"Viajar al pasado: " }
            input(type = InputType.date, name = "date") {
                value = data.date // Preseleccionar la fecha actual
                max = java.time.LocalDate.now().toString() // No viajar al futuro
            }
            button(type = ButtonType.submit) { +"Ir 🚀" }
        }

        // LA TARJETA DE LA FOTO
        div("card") {
            if (data.mediaType == "image") {
                img(src = data.url, alt = data.title)
            } else {
                iframe { src = data.url; width = "100%"; height = "400"; attributes["frameborder"] = "0" }
            }
            div("content") {
                h2 { +data.title }
                span("date") { +"📅 ${data.date}" }
                p { +data.explanation }
            }
        }
    }
}
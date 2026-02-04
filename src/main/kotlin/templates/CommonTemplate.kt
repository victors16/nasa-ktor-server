package com.templates

import kotlinx.html.*

fun HTML.commonLayout(pageTitle: String, pageContent: DIV.() -> Unit) {
    head {
        title { +pageTitle }
        meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
        style {
            unsafe {
                raw("""
                    :root { --bg: #1a1a1a; --card: #2d2d2d; --accent: #61dafb; --text: #fff; }
                    body { font-family: system-ui, sans-serif; background-color: var(--bg); color: var(--text); margin: 0; padding-bottom: 50px; }
                    
                    /* NAVBAR */
                    .navbar { background-color: var(--card); padding: 15px; position: sticky; top: 0; z-index: 100; display: flex; justify-content: center; gap: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.3); }
                    .nav-link { color: var(--text); text-decoration: none; font-weight: bold; padding: 8px 16px; border-radius: 20px; transition: 0.3s; }
                    .nav-link:hover, .nav-link.active { background-color: var(--accent); color: #000; }
                    
                    /* CONTAINER */
                    .container { max-width: 800px; margin: 20px auto; padding: 0 15px; display: flex; flex-direction: column; align-items: center; }
                    
                    /* CARDS */
                    .card { background-color: var(--card); border-radius: 15px; overflow: hidden; margin-bottom: 30px; width: 100%; box-shadow: 0 4px 15px rgba(0,0,0,0.5); }
                    img, iframe { width: 100%; display: block; min-height: 300px; background: #333; }
                    .content { padding: 20px; }
                    h2 { margin: 0 0 10px 0; color: var(--accent); }
                    .date { color: #aaa; font-size: 0.9em; margin-bottom: 10px; display: block; }
                    
                    /* FORM */
                    .date-picker { margin-bottom: 20px; display: flex; gap: 10px; align-items: center; background: var(--card); padding: 10px; border-radius: 10px; }
                    input[type="date"] { padding: 8px; border-radius: 5px; border: none; }
                    button { padding: 8px 15px; background: var(--accent); border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
                    
                    /* LOADER */
                    .loader { text-align: center; padding: 20px; color: #888; display: none; }
                """.trimIndent())
            }
        }
    }
    body {
        // --- BARRA DE NAVEGACIÓN ---
        div("navbar") {
            a(href = "/", classes = "nav-link") { +"📷 Foto del Día" }
            a(href = "/gallery", classes = "nav-link") { +"📚 Galería Infinita" }
        }

        // --- CONTENIDO DE LA PÁGINA ---
        div("container") {
            pageContent()
        }
    }
}
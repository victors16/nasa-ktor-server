package com.templates

import kotlinx.html.*

fun HTML.galleryPage() {
    commonLayout("Galería Infinita") {
        h1 { +"Explorando el Universo" }

        // Aquí JS inyectará las fotos
        div { id = "gallery-container" }

        // Indicador de carga
        div("loader") { id = "loader"; +"Cargando más maravillas... 🪐" }

        // --- EL CEREBRO DE LA PÁGINA (JavaScript) ---
        script {
            unsafe {
                raw("""
                    let offset = 0;
                    const limit = 5; // Cargamos de 5 en 5 para no saturar
                    let isLoading = false;

                    // Función para descargar y pintar fotos
                    async function loadMore() {
                        if (isLoading) return;
                        isLoading = true;
                        document.getElementById('loader').style.display = 'block';

                        try {
                            // Llamamos a TU API JSON
                            const response = await fetch(`/api/nasa/list?offset=` + offset + `&limit=` + limit);
                            const data = await response.json();

                            const container = document.getElementById('gallery-container');
                            
                            data.forEach(item => {
                                // Creamos el HTML de la tarjeta dinámicamente
                                const card = document.createElement('div');
                                card.className = 'card';
                                
                                let mediaHtml = '';
                                if (item.media_type === 'image') {
                                    mediaHtml = '<img src=\"' + item.url + '\" loading=\"lazy\" />';
                                } else {
                                    mediaHtml = '<iframe src=\"' + item.url + '\" height=\"300\" frameborder=\"0\"></iframe>';
                                }

                                card.innerHTML = mediaHtml + 
                                    '<div class=\"content\">' +
                                        '<h2>' + item.title + '</h2>' +
                                        '<span class=\"date\">' + item.date + '</span>' +
                                        '<p>' + item.explanation.substring(0, 150) + '...</p>' +
                                    '</div>';
                                container.appendChild(card);
                            });

                            offset += limit; // Avanzamos el contador para la próxima vez
                        } catch (e) {
                            console.error("Error cargando fotos:", e);
                        } finally {
                            isLoading = false;
                            document.getElementById('loader').style.display = 'none';
                        }
                    }

                    // Detectar Scroll Infinito
                    window.addEventListener('scroll', () => {
                        if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 500) {
                            loadMore();
                        }
                    });

                    // Carga inicial al entrar
                    loadMore();
                """.trimIndent())
            }
        }
    }
}
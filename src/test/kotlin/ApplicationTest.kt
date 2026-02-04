package com

import com.models.ApodResponse
import com.repositories.NasaRepository
import com.services.NasaService
import io.ktor.client.*
import io.ktor.client.engine.mock.*
// OJO: Import del Cliente (para el mock)
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
// OJO: Import del Servidor (para que tu app responda JSON)
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @BeforeTest
    fun setup() {
        stopKoin()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test root endpoint returns HTML web page`() = testApplication {
        // Configuración limpia
        environment { config = MapApplicationConfig() }

        application {
            // Aunque la raíz devuelve HTML, necesitamos esto instalado para las rutas de API
            // que también se configuran en configureRouting()
            install(ServerContentNegotiation) { json() }

            install(Koin) {
                modules(module {
                    // MOCK DE LA NASA (Devuelve JSON crudo, como la API real)
                    single {
                        val mockEngine = MockEngine { _ ->
                            respond(
                                content = """{
                                    "title": "Mocked Space Web",
                                    "url": "https://example.com/fake.jpg",
                                    "media_type": "image",
                                    "explanation": "This data comes from the MockEngine",
                                    "date": "2023-01-01"
                                }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }
                        HttpClient(mockEngine) {
                            install(ClientContentNegotiation) {
                                json(Json { ignoreUnknownKeys = true })
                            }
                        }
                    }
                    single<NasaRepository> { FakeNasaRepository() }
                    singleOf(::NasaService)
                })
            }
            configureRouting()
        }

        // 1. LLAMADA A LA RAÍZ "/"
        val response = client.get("/")

        // 2. VERIFICACIONES
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        // Verificamos que es una WEB (HTML), no un JSON
        assertTrue(body.contains("<!DOCTYPE html>"), "Should be an HTML document")
        assertTrue(body.contains("NASA APOD"), "Should contain the visible title defined in HTML")
        assertTrue(body.contains("Mocked Space Web"), "Should contain the content from the service")
    }

    @Test
    fun `test API endpoint returns JSON`() = testApplication {
        environment { config = MapApplicationConfig() }

        application {
            install(ServerContentNegotiation) { json() }

            install(Koin) {
                modules(module {
                    single {
                        val mockEngine = MockEngine { _ ->
                            respond(
                                content = """{
                                    "title": "API Data",
                                    "url": "https://example.com/api.jpg",
                                    "media_type": "image",
                                    "explanation": "Data for mobile app",
                                    "date": "2023-01-01"
                                }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }
                        HttpClient(mockEngine) {
                            install(ClientContentNegotiation) {
                                json(Json { ignoreUnknownKeys = true })
                            }
                        }
                    }
                    single<NasaRepository> { FakeNasaRepository() }
                    singleOf(::NasaService)
                })
            }
            configureRouting()
        }

        // 1. LLAMADA A LA RUTA DE API (fíjate en el /api/)
        val response = client.get("/api/nasa/today")

        // 2. VERIFICACIONES
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()

        // Verificamos que es JSON puro (empieza por llave o corchete)
        assertTrue(body.trim().startsWith("{"), "Should be a JSON object")
        assertTrue(body.contains("media_type"), "Should contain JSON keys")
    }

    @Test
    fun `test specific date API endpoint`() = testApplication {
        environment { config = MapApplicationConfig() }

        application {
            install(ServerContentNegotiation) { json() }

            install(Koin) {
                modules(module {
                    single {
                        HttpClient(MockEngine { request ->
                            // Validamos que el servicio envíe el parámetro date correctamente a la NASA
                            if (request.url.parameters.contains("date", "2020-01-01")) {
                                respond(
                                    content = """{
                                        "title": "Archive Photo",
                                        "date": "2020-01-01",
                                        "url": "...",
                                        "media_type": "image",
                                        "explanation": "..."
                                    }""",
                                    status = HttpStatusCode.OK,
                                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                                )
                            } else {
                                respondError(HttpStatusCode.NotFound)
                            }
                        }) { install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
                    }
                    single<NasaRepository> { FakeNasaRepository() }
                    singleOf(::NasaService)
                })
            }
            configureRouting()
        }

        // Probamos la ruta específica de API
        val response = client.get("/api/nasa/2020-01-01")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Archive Photo"))
    }
}

// ... FakeNasaRepository ...
class FakeNasaRepository : NasaRepository {
    private val storage = mutableMapOf<String, ApodResponse>()
    override fun findByDate(date: String) = storage[date]
    override fun save(response: ApodResponse) { storage[response.date] = response }
    override fun init() {}
}
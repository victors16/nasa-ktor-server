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
    fun `test root endpoint returns todays photo`() = testApplication {

        environment { config = MapApplicationConfig() }
        application {
            install(ServerContentNegotiation) { json() }

            install(Koin) {
                modules(module {
                    single {
                        val mockEngine = MockEngine { _ ->
                            respond(
                                content = """{
                                    "title": "Mocked Space",
                                    "url": "https://example.com/fake.jpg",
                                    "media_type": "image",
                                    "explanation": "This data comes from the MockEngine",
                                    "date": "2023-01-01"
                                }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }
                        // 3. Configurar el Cliente Mock
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

        // AHORA PROBAMOS LA RAÍZ "/"
        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        // Verificamos que devuelve algo (en el mock poníamos fecha fija o dinámica)
        assertTrue(response.bodyAsText().contains("media_type"))
    }

    @Test
    fun `test root nasa endpoint returns success using Mocks`() = testApplication {
        environment { config = MapApplicationConfig() }

        application {
            // 1. INSTALAR SERIALIZACIÓN EN EL SERVIDOR (¡ESTO FALTABA!)
            // Sin esto, el servidor no puede responder JSON -> Error 406
            install(ServerContentNegotiation) {
                json()
            }

            // 2. Instalar Koin
            install(Koin) {
                modules(module {
                    single {
                        val mockEngine = MockEngine { _ ->
                            respond(
                                content = """{
                                    "title": "Mocked Space",
                                    "url": "https://example.com/fake.jpg",
                                    "media_type": "image",
                                    "explanation": "This data comes from the MockEngine",
                                    "date": "2023-01-01"
                                }""",
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }
                        // 3. Configurar el Cliente Mock
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

        val response = client.get("/nasa/today")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Mocked Space"))
    }

    @Test
    fun `test specific date endpoint`() = testApplication {
        environment { config = MapApplicationConfig() }

        application {
            // AQUÍ TAMBIÉN FALTA EL SERVER CONTENT NEGOTIATION
            install(ServerContentNegotiation) {
                json()
            }

            install(Koin) {
                modules(module {
                    single {
                        HttpClient(MockEngine { request ->
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

        val response = client.get("/nasa/2020-01-01")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

// ... FakeNasaRepository ...
class FakeNasaRepository : NasaRepository {
    private val storage = mutableMapOf<String, ApodResponse>()
    override fun findByDate(date: String) = storage[date]
    override fun save(response: ApodResponse) { storage[response.date] = response }
    override fun init() {}
}
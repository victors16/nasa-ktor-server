package com.di

import com.repositories.NasaRepository
import com.repositories.NasaRepositoryImpl
import com.services.NasaService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf // Magic function for cleaner code
import org.koin.dsl.module

val appModule = module {

    // 1. NETWORK MODULE
    // We define how to create the HttpClient.
    // "single" means Koin will create ONE instance and reuse it (Singleton).
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }

    // 2. DATABASE / REPOSITORY MODULE
    // We bind the Interface to the Implementation (Best practice for testing)
    single<NasaRepository> { NasaRepositoryImpl() }

    // 3. SERVICE MODULE
    // "singleOf" detects that NasaService needs a Client and a Repository
    // and injects them automatically from the definitions above.
    singleOf(::NasaService)
}
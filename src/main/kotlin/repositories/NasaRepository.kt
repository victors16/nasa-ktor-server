package com.repositories


import com.models.ApodResponse
import com.models.NasaCacheTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface NasaRepository {
    fun findByDate(date: String): ApodResponse?
    fun save(response: ApodResponse)
    fun init()
}

class NasaRepositoryImpl : NasaRepository {

    // Initialize DB connection (In a real app, do this in Application.module)
    override fun init() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(NasaCacheTable)
        }
    }

    override fun findByDate(date: String): ApodResponse? {
        return transaction {
            NasaCacheTable.selectAll().where { NasaCacheTable.date eq date }
                .map {
                    ApodResponse(
                        title = it[NasaCacheTable.title],
                        explanation = it[NasaCacheTable.explanation],
                        url = it[NasaCacheTable.url],
                        mediaType = it[NasaCacheTable.mediaType],
                        date = it[NasaCacheTable.date]
                    )
                }
                .singleOrNull()
        }
    }

    override fun save(response: ApodResponse) {
        transaction {
            NasaCacheTable.upsert { // upsert = update if exists, insert if not
                it[date] = response.date
                it[title] = response.title
                it[explanation] = response.explanation
                it[url] = response.url
                it[mediaType] = response.mediaType
            }
        }
    }
}
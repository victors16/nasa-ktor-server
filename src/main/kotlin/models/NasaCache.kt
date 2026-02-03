package com.models

import org.jetbrains.exposed.sql.Table

/**
 * Database definition for caching NASA APOD responses.
 */
object NasaCacheTable : Table("nasa_cache") {
    val date = varchar("date", 10) // PK: YYYY-MM-DD
    val title = varchar("title", 255)
    val explanation = text("explanation") // Text for long descriptions
    val url = varchar("url", 500)
    val mediaType = varchar("media_type", 50)

    override val primaryKey = PrimaryKey(date)
}
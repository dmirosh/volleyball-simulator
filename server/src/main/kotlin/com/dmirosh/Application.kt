package com.dmirosh

import com.dmirosh.model.AppStateDto
import com.dmirosh.model.SavedAppState
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.json
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(UnstableDefault::class)
val json = Json(
    JsonConfiguration.Default
)

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        json(
            contentType = ContentType.Application.Json,
            json = Json(
                DefaultJsonConfiguration.copy(
                    prettyPrint = true,
                    useArrayPolymorphism = false
                )
            )
        )
    }

    initDB()

    routing {
        static {
            defaultResource("index.html")
            resources()
        }

        get("/api/game") {
            val allGames = GameRepository.findGames().map {
                SavedAppState(
                    id = it.id,
                    name = it.name,
                    appState = json.parse(AppStateDto.serializer(), it.state)
                )
            }
            call.respond(allGames)
        }

        post("/api/game") {
            val newGame = call.receive<String>()
            val savedGame = GameRepository.saveGame(newGame)
            call.respond(
                SavedAppState(
                    id = savedGame.id,
                    name = savedGame.name,
                    appState = json.parse(AppStateDto.serializer(), savedGame.state)
                )
            )
        }

        delete("/api/game/{gameId}") {
            val id = call.parameters["gameId"]!!.toInt()
            GameRepository.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun initDB() {
    Database.connect("jdbc:h2:~/volleyball.db;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
    withTransaction {
        SchemaUtils.createMissingTablesAndColumns(GameTable)
    }
}

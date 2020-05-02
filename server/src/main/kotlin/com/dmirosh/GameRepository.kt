package com.dmirosh

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun <T> withTransaction(body: () -> T): T {
    return transaction {
        addLogger(StdOutSqlLogger)
        body()
    }
}

data class GameEntity(
    val id: Int,
    val name: String,
    val state: String
)

object GameRepository {

    fun findGames(): List<GameEntity> = withTransaction {
        GameTable.selectAll()
            .map { map(it) }
    }

    fun saveGame(newGame: String): GameEntity = withTransaction {
        val id = GameTable.insert {
            it[name] = createName()
            it[state] = newGame
        }[GameTable.id]

        GameTable.select { GameTable.id eq id }
            .map { map(it) }
            .first()
    }

    private fun createName(): String {
        val now = LocalDateTime.now()
        return "${now.month}-${now.dayOfMonth}-${now.hour}-${now.minute}-${now.second}"
    }

    private fun map(resultRow: ResultRow): GameEntity {
        return GameEntity(
            id = resultRow[GameTable.id],
            name = resultRow[GameTable.name],
            state = resultRow[GameTable.state]
        )
    }

    fun delete(id: Int) = withTransaction {
        GameTable.deleteWhere { GameTable.id eq id }
    }
}
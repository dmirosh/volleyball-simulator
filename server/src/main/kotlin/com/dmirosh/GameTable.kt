package com.dmirosh

import org.jetbrains.exposed.sql.Table

object GameTable : Table("game") {
    val id = integer("id").autoIncrement()

    val name = text("name")

    val state = text("state")

    override val primaryKey = PrimaryKey(id)
}
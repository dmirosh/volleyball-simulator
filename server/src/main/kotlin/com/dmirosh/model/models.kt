package com.dmirosh.model

import kotlinx.serialization.Serializable

@Serializable
data class AppStateDto(
    var game: Game,
    var notes: MutableList<Note>,
    var commandManager: CommandManager
)

@Serializable
data class SavedAppState(
    var id: Int,
    val name: String,
    var appState: AppStateDto
)

@Serializable
data class Note(
    val content: String
)

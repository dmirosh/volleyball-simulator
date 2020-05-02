package note

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val content: String
)
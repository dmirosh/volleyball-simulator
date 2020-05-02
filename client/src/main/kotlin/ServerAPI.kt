import command.CommandManager
import game.Game
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import note.Note
import org.w3c.fetch.RequestInit
import kotlin.browser.window
import kotlin.js.json

@Serializable
data class AppStateDto(
    var game: Game,
    var notes: MutableList<Note>,
    var commandManager: CommandManager
)

@Serializable
data class SavedAppState(
    val id: Int,
    val name: String,
    val appState: AppStateDto
)

@UnstableDefault
object ServerAPI {
    val jsonSerializer = Json(
        JsonConfiguration.Default
    )

    @ImplicitReflectionSerializer
    fun getGames(consumer: (List<SavedGame>) -> Unit) {
        window.fetch(
            "/api/game", RequestInit(
                "GET", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                )
            )
        ).then { response ->
            response.text().then { responseBody ->
                try {
                    val games = jsonSerializer.parse(SavedAppState.serializer().list, responseBody)
                        .map {
                            SavedGame(
                                id = it.id,
                                current = false,
                                name = it.name,
                                state = AppState(
                                    game = mutableListOf(it.appState.game),
                                    commandManager = it.appState.commandManager,
                                    notes = it.appState.notes,
                                    savedGames = mutableListOf()
                                )
                            )
                        }
                    consumer(games)
                } catch (ex: Exception) {
                    println("Can't parse $responseBody")
                    println(ex)
                }
            }
        }
    }

    @ImplicitReflectionSerializer
    fun saveGame(appState: AppState, onSaved: (Int, String, AppState) -> Unit) {
        val req = AppStateDto(
            game = appState.game.first(),
            notes = appState.notes,
            commandManager = appState.commandManager
        )
        window.fetch(
            "/api/game", RequestInit(
                "POST",
                headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                ),
                body = jsonSerializer.stringify(req)
            )
        ).then { response ->
            response.text().then { responseBody ->
                try {
                    val game = jsonSerializer.parse(SavedAppState.serializer(), responseBody)
                    onSaved(
                        game.id, game.name, AppState(
                            game = mutableListOf(game.appState.game),
                            commandManager = game.appState.commandManager,
                            notes = game.appState.notes,
                            savedGames = mutableListOf()
                        )
                    )
                } catch (ex: Exception) {
                    println("Can't parse $responseBody")
                    println(ex)
                }
            }
        }
    }

    fun deleteGame(gameId: Int, onDeleted: () -> Unit) {
        window.fetch(
            "/api/game/$gameId", RequestInit(
                "DELETE",
                headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                )
            )
        ).then { onDeleted() }
    }
}
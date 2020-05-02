import command.Command
import command.CommandManager
import command.commandsList
import game.Game
import game.Player
import game.view.game
import game.view.score
import kotlinx.html.classes
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import note.Note
import note.notesList
import org.w3c.dom.HTMLImageElement
import react.*
import react.dom.div
import kotlin.browser.document

data class SavedGame(
    val id: Int,
    var current: Boolean,
    val name: String,
    val state: AppState
)

data class AppState(
    var game: MutableList<Game>,
    var notes: MutableList<Note>,
    var savedGames: MutableList<SavedGame>,
    var commandManager: CommandManager
) : RState

@ImplicitReflectionSerializer
class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        commandManager = CommandManager()
        game = mutableListOf(Game().also { it.reset() })
        savedGames = mutableListOf()
        notes = mutableListOf()
    }

    @OptIn(UnstableDefault::class)
    override fun RBuilder.render() {
        game {
            game = state.game
            fieldImage = document.getElementById("field") as HTMLImageElement
            onSwitchTeam = { switchTeamHandler() }
            onSuccessfulTurn = { successfulTurnHandler() }
            onFailedTurn = { failureTurnHandler() }
            onPlayerMoveToField = { moveToFieldHandler(it) }
            onPlayerReturnToBench = { returnToBenchHandler(it) }
            onPlayerOnPositionChanged = { newPlayer, position, oldPlayer ->
                playerPositionChangedHandler(newPlayer, position, oldPlayer)
            }
            onLiberoMoveToField = { newLibero, oldLibero -> liberoMoveToFieldHandler(newLibero, oldLibero) }
        }
        score {
            score = state.game.first().score
        }
        controlPanel {
            games = state.savedGames
            onSaveGame = {
                saveGameHandler()
            }
            onDeleteGame = {
                deleteGameHandler(it)
            }
            onLoadGames = {
                loadGameHandler()
            }
            onGoToGame = {
                goToGameHandler(it)
            }
        }
        div {
            attrs.classes = setOf("clear")
        }
        commandsList {
            commands = state.commandManager.commands
            currentCommandIndex = state.commandManager.currentCommandIndex
            onNextCommand = {
                state.commandManager.nextCommandHandler(state.game.first())
                updateState()
            }
            onPreviousCommand = {
                state.commandManager.previousCommandHandler(state.game.first())
                updateState()
            }
            onGoToCommand = { commandIndex ->
                state.commandManager.goToCommandHandler(state.game.first(), commandIndex)
                updateState()
            }
        }
        notesList {
            notes = state.notes
            onAddNote = {
                state.notes.add(Note(it))
                updateState()
            }
        }
    }

    private fun goToGameHandler(gameId: Int) {
        val savedGame = state.savedGames.first { it.id == gameId }
        setState {
            state.savedGames.forEach { it.current = false }
            state.savedGames.filter { it.id == gameId }.forEach { it.current = true }
            state.commandManager.change(savedGame.state.commandManager)

            state.game.clear()
            state.game.addAll(savedGame.state.game)

            state.notes.clear()
            state.notes.addAll(savedGame.state.notes)
        }
    }

    @OptIn(UnstableDefault::class)
    private fun loadGameHandler() {
        ServerAPI.getGames { newGames ->
            setState {
                state.savedGames.clear()
                state.savedGames.addAll(newGames.toMutableList())
            }
        }
    }

    @OptIn(UnstableDefault::class)
    private fun deleteGameHandler(gameId: Int) {
        ServerAPI.deleteGame(gameId) {
            setState {
                state.savedGames.removeAll { it.id == gameId }
            }
        }
    }

    @OptIn(UnstableDefault::class)
    private fun saveGameHandler() {
        ServerAPI.saveGame(state) { id, name, savedState ->
            setState {
                state.savedGames.add(
                    SavedGame(
                        id = id,
                        name = name,
                        state = savedState,
                        current = false
                    )
                )
            }
        }
    }

    private fun liberoMoveToFieldHandler(newLibero: Player, oldLibero: Player?) {
        state.commandManager.executeAndAddCommand(
            state.game.first(),
            Command.MoveLiberoToField(newLibero.teamName, newLibero.name, oldLibero?.name)
        )
        updateState()
    }

    private fun playerPositionChangedHandler(newPlayer: Player, position: Int, oldPlayer: Player?) {
        state.commandManager.executeAndAddCommand(
            state.game.first(),
            Command.ChangePlayers(newPlayer.teamName, newPlayer.name, position, oldPlayer?.name)
        )
        updateState()
    }

    private fun returnToBenchHandler(player: Player) {
        state.commandManager.executeAndAddCommand(
            state.game.first(),
            Command.ReturnToBench(player.teamName, player.name, player.positionOnField!!)
        )
        updateState()
    }

    private fun moveToFieldHandler(player: Player) {
        val firstFreePosition = state.game.first().firstFreePosition(player.teamName)
        if (firstFreePosition != null) {
            state.commandManager.executeAndAddCommand(
                state.game.first(),
                Command.MoveToField(player.teamName, player.name, firstFreePosition)
            )
            updateState()
        }
    }

    private fun failureTurnHandler() {
        state.commandManager.executeAndAddCommand(
            state.game.first(),
            Command.FailedTurn(state.game.first().currentTeam)
        )
        updateState()
    }

    private fun successfulTurnHandler() {
        state.commandManager.executeAndAddCommand(
            state.game.first(),
            Command.SuccessfulTurn(state.game.first().currentTeam)
        )
        updateState()
    }

    private fun switchTeamHandler() {
        state.commandManager.executeAndAddCommand(
            state.game.first(),
            Command.SwitchTurn
        )
        updateState()
    }

    private fun updateState() {
        setState({
            it.game = it.game
            it.commandManager = it.commandManager
            it.notes = it.notes
            it
        })
    }
}

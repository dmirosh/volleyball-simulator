import command.Command
import command.CommandManager
import command.commandsList
import game.Game
import game.Player
import game.view.game
import game.view.score
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import note.Note
import note.notesList
import org.w3c.dom.HTMLImageElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import kotlin.browser.document


@Serializable
data class AppState(
    var game: Game,
    var notes: MutableList<Note>,
    var commandManager: CommandManager
) : RState

@ImplicitReflectionSerializer
class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        commandManager = CommandManager()
        game = Game()
        notes = mutableListOf()
    }

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
            score = state.game.score
        }
        commandsList {
            commands = state.commandManager.commands
            currentCommandIndex = state.commandManager.currentCommandIndex
            onNextCommand = {
                state.commandManager.nextCommandHandler(state.game)
                updateState()
            }
            onPreviousCommand = {
                state.commandManager.previousCommandHandler(state.game)
                updateState()
            }
            onGoToCommand = { commandIndex ->
                state.commandManager.goToCommandHandler(state.game, commandIndex)
                updateState()
            }
        }
        div {

        }
        notesList {
            notes = state.notes
            onAddNote = {
                state.notes.add(Note(it))
                updateState()
            }
        }
        div {
            attrs.classes = setOf("block")
            button {
                attrs.onClickFunction = {
                    println(AppStateStorage.serialize(state))
                }
                +"Сохранить"
            }
        }
    }

    private fun liberoMoveToFieldHandler(newLibero: Player, oldLibero: Player?) {
        state.commandManager.executeAndAddCommand(
            state.game,
            Command.MoveLiberoToField(newLibero, oldLibero)
        )
        updateState()
    }

    private fun playerPositionChangedHandler(newPlayer: Player, position: Int, oldPlayer: Player?) {
        state.commandManager.executeAndAddCommand(
            state.game,
            Command.ChangePlayers(newPlayer, position, oldPlayer)
        )
        updateState()
    }

    private fun returnToBenchHandler(player: Player) {
        state.commandManager.executeAndAddCommand(
            state.game,
            Command.ReturnToBench(player, player.positionOnField!!)
        )
        updateState()
    }

    private fun moveToFieldHandler(player: Player) {
        val firstFreePosition = state.game.firstFreePosition(player.teamName)
        if (firstFreePosition != null) {
            state.commandManager.executeAndAddCommand(
                state.game,
                Command.MoveToField(player, firstFreePosition)
            )
            updateState()
        }
    }

    private fun failureTurnHandler() {
        state.commandManager.executeAndAddCommand(
            state.game,
            Command.FailedTurn(state.game.currentTeam)
        )
        updateState()
    }

    private fun successfulTurnHandler() {
        state.commandManager.executeAndAddCommand(
            state.game,
            Command.SuccessfulTurn(state.game.currentTeam)
        )
        updateState()
    }

    private fun switchTeamHandler() {
        state.commandManager.executeAndAddCommand(
            state.game,
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

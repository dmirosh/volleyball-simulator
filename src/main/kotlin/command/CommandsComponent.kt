package command

import game.LIBERO_POSITION
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.div
import react.dom.span

fun RBuilder.commandsList(handler: CommandsProps.() -> Unit): ReactElement {
    return child(CommandsComponent::class) {
        this.attrs(handler)
    }
}

external interface CommandsProps : RProps {
    var commands: List<Command>
    var currentCommandIndex: Int?

    var onNextCommand: () -> Unit
    var onPreviousCommand: () -> Unit
    var onGoToCommand: (Int) -> Unit
}

class CommandsComponent : RComponent<CommandsProps, RState>() {
    override fun RBuilder.render() {
        div {
            attrs.classes = setOf("block")
            div {
                button {
                    attrs.onClickFunction = { props.onPreviousCommand() }
                    attrs.disabled = props.currentCommandIndex == -1
                    +"Предыдущая команда"
                }
                button {
                    attrs.onClickFunction = { props.onNextCommand() }
                    attrs.disabled =
                        props.commands.isEmpty() || props.currentCommandIndex!! == props.commands.lastIndex
                    +"Следующая команда"
                }
            }
            props.commands.forEachIndexed { index, command ->
                val lastCommandMarker = if (props.currentCommandIndex == index) ">" else ""
                div {
                    span {
                        +"$lastCommandMarker ${index + 1}: ${commandDescription(command)}"
                    }
                    button {
                        attrs.onClickFunction = { props.onGoToCommand(index) }
                        +"Перейти на команду"
                    }
                }

            }
        }
    }

    private fun commandDescription(command: Command): String = when (command) {
        Command.SwitchTurn -> "Смена стороны"
        is Command.ReturnToBench -> "${command.player.teamName}: Игрок ${command.player.name} покинул позицию ${command.position}"
        is Command.MoveToField -> when (command.position) {
            LIBERO_POSITION -> "${command.player.teamName}: Игрок ${command.player.name} стал либеро"
            else -> "${command.player.teamName}: Игрок ${command.player.name} занял позицию ${command.position}"
        }
        is Command.MoveLiberoToField -> when (command.oldLibero) {
            null -> "${command.newLibero.teamName}: Игрок ${command.newLibero.name} стал либеро"
            else -> "${command.newLibero.teamName}: Замена либеро с ${command.oldLibero.name} на ${command.newLibero.name}"
        }
        is Command.SuccessfulTurn -> "${command.team}: Успешная подача"
        is Command.FailedTurn -> "${command.team}: Неуспешная подача"
        is Command.ChangePlayers -> when (command.oldPlayer) {
            null -> "${command.newPlayer.teamName}: Игрок ${command.newPlayer.name} занял позицию ${command.position}"
            else -> "${command.newPlayer.teamName}: Замена игрока с ${command.oldPlayer.name} на ${command.newPlayer.name}"
        }
    }
}
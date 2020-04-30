package command

import game.Game
import kotlin.math.max

data class CommandManager(
    var commands: MutableList<Command> = mutableListOf(),
    var currentCommandIndex: Int = -1
) {

    fun executeAndAddCommand(game: Game, command: Command) {
        if (currentCommandIndex == -1 && commands.isNotEmpty()) {
            commands.clear()
        } else if (currentCommandIndex != commands.lastIndex) {
            commands = commands.subList(0, currentCommandIndex + 1)
        }
        command.execute(game)
        commands.add(command)
        currentCommandIndex++
    }

    fun goToCommandHandler(game: Game, commandIndex: Int) {
        if (commandIndex < 0 || commandIndex > commands.lastIndex || commandIndex == currentCommandIndex) {
            return
        }
        if (commandIndex > currentCommandIndex) {
            for (i in max(currentCommandIndex, 0)..commandIndex) {
                commands[i].execute(game)
            }
        } else {
            for (i in currentCommandIndex downTo commandIndex + 1) {
                commands[i].rollback(game)
            }
        }
        currentCommandIndex = commandIndex
    }

    fun previousCommandHandler(game: Game) {
        if (currentCommandIndex == -1) {
            return
        }
        commands[currentCommandIndex].rollback(game)
        currentCommandIndex--
    }

    fun nextCommandHandler(game: Game) {
        if (currentCommandIndex == commands.lastIndex) {
            return
        }
        currentCommandIndex++
        commands[currentCommandIndex].execute(game)
    }
}
package command

import game.Game
import game.Player
import game.TeamName

sealed class Command {
    abstract fun execute(game: Game)
    abstract fun rollback(game: Game)

    object SwitchTurn : Command() {
        override fun execute(game: Game) {
            game.switchTurn()
        }

        override fun rollback(game: Game) {
            game.switchTurn()
        }
    }

    data class ReturnToBench(val player: Player, val position: Int) : Command() {
        override fun execute(game: Game) {
            game.removeFromField(player)
        }

        override fun rollback(game: Game) {
            game.moveToField(player, position)
        }
    }

    data class MoveToField(
        val player: Player,
        val position: Int
    ) : Command() {
        override fun execute(game: Game) {
            game.moveToField(player, position)
        }

        override fun rollback(game: Game) {
            game.removeFromField(player)
        }
    }

    data class ChangePlayers(
        val newPlayer: Player,
        val position: Int,
        val oldPlayer: Player?
    ) : Command() {
        override fun execute(game: Game) {
            game.moveToField(newPlayer, position)
        }

        override fun rollback(game: Game) {
            game.removeFromField(newPlayer)
            if (oldPlayer != null) {
                game.moveToField(oldPlayer, position)
            }
        }
    }

    data class MoveLiberoToField(val newLibero: Player, val oldLibero: Player?) : Command() {
        override fun execute(game: Game) {
            game.moveLiberoToField(newLibero.teamName, newLibero, oldLibero)
        }

        override fun rollback(game: Game) {
            game.moveLiberoToField(newLibero.teamName, oldLibero, newLibero)
        }
    }

    data class SuccessfulTurn(val team: TeamName) : Command() {
        override fun execute(game: Game) {
            game.makeTurnSuccess()
        }

        override fun rollback(game: Game) {
            game.removeTurnSuccess()
        }
    }

    data class FailedTurn(val team: TeamName) : Command() {
        override fun execute(game: Game) {
            game.makeTurnFailure()
        }

        override fun rollback(game: Game) {
            game.removeFailureTurn()
        }
    }
}
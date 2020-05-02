package command

import game.Game
import game.Player
import game.TeamName
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract sealed class Command {
    abstract fun execute(game: Game)
    abstract fun rollback(game: Game)

    @Serializable
    @SerialName("SwitchTurn")
    object SwitchTurn : Command() {
        override fun execute(game: Game) {
            game.switchTurn()
        }

        override fun rollback(game: Game) {
            game.switchTurn()
        }
    }

    @Serializable
    @SerialName("ReturnToBench")
    data class ReturnToBench(
        val team: TeamName,
        val player: String,
        val position: Int) : Command() {
        override fun execute(game: Game) {
            game.removeFromField(team, player)
        }

        override fun rollback(game: Game) {
            game.moveToField(team, player, position)
        }
    }

    @Serializable
    @SerialName("MoveToField")
    data class MoveToField(
        val team: TeamName,
        val player: String,
        val position: Int
    ) : Command() {
        override fun execute(game: Game) {
            game.moveToField(team, player, position)
        }

        override fun rollback(game: Game) {
            game.removeFromField(team, player)
        }
    }

    @Serializable
    @SerialName("ChangePlayers")
    data class ChangePlayers(
        val team: TeamName,
        val newPlayer: String,
        val position: Int,
        val oldPlayer: String?
    ) : Command() {
        override fun execute(game: Game) {
            game.moveToField(team,newPlayer, position)
        }

        override fun rollback(game: Game) {
            game.removeFromField(team,newPlayer)
            if (oldPlayer != null) {
                game.moveToField(team, oldPlayer, position)
            }
        }
    }

    @Serializable
    @SerialName("MoveLiberoToField")
    data class MoveLiberoToField(val team: TeamName,
                                 val newLibero: String, val oldLibero: String?) : Command() {
        override fun execute(game: Game) {
            game.moveLiberoToField(team, newLibero, oldLibero)
        }

        override fun rollback(game: Game) {
            game.moveLiberoToField(team, oldLibero, newLibero)
        }
    }

    @Serializable
    @SerialName("SuccessfulTurn")
    data class SuccessfulTurn(val team: TeamName) : Command() {
        override fun execute(game: Game) {
            game.makeTurnSuccess()
        }

        override fun rollback(game: Game) {
            game.removeTurnSuccess()
        }
    }

    @Serializable
    @SerialName("FailedTurn")
    data class FailedTurn(val team: TeamName) : Command() {
        override fun execute(game: Game) {
            game.makeTurnFailure()
        }

        override fun rollback(game: Game) {
            game.removeFailureTurn()
        }
    }
}
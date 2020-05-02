package game

import kotlinx.serialization.Serializable

const val PLAYERS_ON_FIELD_COUNT = 6
const val LIBERO_POSITION = -1

@Serializable
enum class TeamName {
    TEAM_A,
    TEAM_B
}

fun TeamName.otherTeam(): TeamName = when (this) {
    TeamName.TEAM_A -> TeamName.TEAM_B
    TeamName.TEAM_B -> TeamName.TEAM_A
}


@Serializable
data class Player(
    val name: String,
    val teamName: TeamName,
    var positionOnField: Int?,
    var libero: Boolean
)


@Serializable
class Team(
    val name: TeamName,
    var players: List<Player> = listOf()
) {
    init {
        if (players.isEmpty()) {
            players = defaultTeam(name)
        }
    }

    private fun defaultTeam(teamName: TeamName): List<Player> {
        return (0..11).map {
            Player(
                teamName = teamName,
                name = "${it + 1}",
                libero = false,
                positionOnField = null
            )
        }
    }

    fun removeLiberoFromField() {
        val libero = players.firstOrNull { it.libero }
        val playerOnLiberoPosition = players.firstOrNull { it.positionOnField == LIBERO_POSITION }
        playerOnLiberoPosition?.positionOnField = PLAYERS_ON_FIELD_COUNT - 1
        libero?.positionOnField = LIBERO_POSITION
    }

    fun addLiberoToField() {
        val libero = players.firstOrNull { it.libero }
        val lastPlayer = players.firstOrNull { it.positionOnField == PLAYERS_ON_FIELD_COUNT - 1 }
        libero?.positionOnField = PLAYERS_ON_FIELD_COUNT - 1
        lastPlayer?.positionOnField = LIBERO_POSITION
    }

    fun shift() {
        val playersOnField = sortedFieldPlayers
        val lastPosition = playersOnField[PLAYERS_ON_FIELD_COUNT - 1].positionOnField
        for (i in PLAYERS_ON_FIELD_COUNT - 1 downTo 1) {
            playersOnField[i].positionOnField = playersOnField[i - 1].positionOnField
        }
        playersOnField[0].positionOnField = lastPosition
    }

    fun unshift() {
        val playersOnField = sortedFieldPlayers
        val firstPosition = playersOnField.first().positionOnField
        for (i in 0 until playersOnField.lastIndex) {
            playersOnField[i].positionOnField = playersOnField[i + 1].positionOnField
        }
        playersOnField.last().positionOnField = firstPosition
    }

    private val sortedFieldPlayers: List<Player>
        get() = players.filter { it.positionOnField != null && it.positionOnField != LIBERO_POSITION }
            .sortedBy { it.positionOnField }

    fun moveLiberoToField(newLibero: Player?, unused: Player?) {
        players.forEach { it.libero = false }
        players.filter { it.positionOnField == LIBERO_POSITION }.forEach { it.positionOnField = null }
        if (newLibero != null) {
            newLibero.libero = true
            newLibero.positionOnField = LIBERO_POSITION
        }
    }

    fun moveToField(player: Player, position: Int) {
        if (player.positionOnField != null) {
            return
        }
        players.filter { it.positionOnField == position }.forEach { it.positionOnField = null }
        player.positionOnField = position
        if (position == LIBERO_POSITION) {
            players.filter { it.libero }.forEach { it.libero = false }
            player.libero = true
        }
    }

    fun removeFromField(player: Player) {
        if (player.libero) {
            player.libero = false
            if (player.positionOnField != LIBERO_POSITION) {
                players.filter { it.positionOnField == LIBERO_POSITION }.forEach { it.libero = true }

            }
        }
        player.positionOnField = null
    }

    fun ready(): Boolean {
        return players.firstOrNull { it.libero } != null &&
                players.count { it.positionOnField != null && it.positionOnField != LIBERO_POSITION } == PLAYERS_ON_FIELD_COUNT
    }

    fun playerOnPosition(position: Int): Player? {
        return players.firstOrNull { it.positionOnField == position }
    }

    fun firstFreePosition(): Int? {
        var pos = (0 until PLAYERS_ON_FIELD_COUNT).firstOrNull { pos -> !players.any { it.positionOnField == pos } }
        if (pos == null && players.none { it.libero }) {
            pos = LIBERO_POSITION
        }
        return pos
    }
}
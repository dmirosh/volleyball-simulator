package game.view

import game.TeamName

object CoordsManager {

    val horizontalLineMargine = 10
    val horizontalSlotMargine = 10

    val verticalLineMargine = 40
    val verticalSlotMargine = 20

    fun getBenchPlayerCoords(team: TeamName, playerIndex: Int): Position {
        val row = playerIndex / 6
        val col = playerIndex % 6
        val x = if (team == TeamName.TEAM_A) {
            FIELD_WIDTH + 2 * horizontalLineMargine + 2 * horizontalLineMargine + SLOT_RADIUS + col * (2 * SLOT_RADIUS + horizontalSlotMargine)
        } else {
            FIELD_WIDTH + 2 * horizontalLineMargine + 2 * horizontalLineMargine + SLOT_RADIUS + col * (2 * SLOT_RADIUS + horizontalSlotMargine)
        }
        val y = if (team == TeamName.TEAM_A) {
            0.0 + verticalLineMargine + SLOT_RADIUS + row * (2 * SLOT_RADIUS + verticalSlotMargine)
        } else {
            FIELD_HEIGHT / 2 + verticalLineMargine + verticalLineMargine + SLOT_RADIUS + row * (2 * SLOT_RADIUS + verticalSlotMargine)
        }
        return Position(x, y)
    }

    fun getFieldPlayerCoords(team: TeamName, playerIndex: Int): Position {
        if (playerIndex == -1) {
            val firstPlayerCoords = getFieldPlayerCoords(team, 0)
            return if (team == TeamName.TEAM_A) {
                firstPlayerCoords.copy(y = firstPlayerCoords.y - 4 * SLOT_RADIUS - horizontalLineMargine)
            } else {
                firstPlayerCoords.copy(y = firstPlayerCoords.y + 4 * SLOT_RADIUS + horizontalLineMargine)
            }
        }
        val row = playerIndex / 3
        val col = playerIndex % 3
        val x = 60 + 2 * horizontalLineMargine + SLOT_RADIUS + col * (2 * SLOT_RADIUS + horizontalSlotMargine)
        val y = if (team == TeamName.TEAM_A) {
            60 + verticalLineMargine + SLOT_RADIUS + row * (2 * SLOT_RADIUS + verticalSlotMargine)
        } else {
            420 - verticalLineMargine - SLOT_RADIUS - row * (2 * SLOT_RADIUS + verticalSlotMargine)
        }
        return Position(x, y)
    }

    fun mapIndexToPositionOnField(team: TeamName, index: Int): Int {
        return when (team) {
            TeamName.TEAM_A -> when (index) {
                0 -> 0
                1 -> 5
                2 -> 4
                3 -> 1
                4 -> 2
                5 -> 3
                else -> throw IllegalArgumentException()
            }
            TeamName.TEAM_B -> when (index) {
                0 -> 4
                1 -> 5
                2 -> 0
                3 -> 3
                4 -> 2
                5 -> 1
                else -> throw IllegalArgumentException()
            }
        }
    }
}
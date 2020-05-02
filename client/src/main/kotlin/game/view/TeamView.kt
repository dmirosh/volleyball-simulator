package game.view

import org.w3c.dom.CanvasRenderingContext2D

data class TeamView(
    val fieldPlayers: List<PlayerSlot>,
    val benchPlayers: List<PlayerSlot>
) {
    fun draw(ctx: CanvasRenderingContext2D) {
        fieldPlayers.forEach { it.draw(ctx) }
        benchPlayers.forEach { it.draw(ctx) }
    }

    fun fieldPlayerWithPosition(position: Position): PlayerSlot? {
        return fieldPlayers.firstOrNull { it.intercepts(position) }
    }

    fun benchPlayerWithPosition(position: Position): PlayerSlot? {
        return benchPlayers.firstOrNull { it.intercepts(position) }
    }
}

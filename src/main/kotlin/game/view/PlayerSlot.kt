package game.view

import game.Player
import game.TeamName
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

const val DEFAULT_FONT = "24px Arial"
const val TEAM_A_COLOR = "blue"
const val TEAM_B_COLOR = "red"
const val DEFAULT_COLOR = "black"
const val LIBERO_COLOR = "green"

data class PlayerSlot(
    val position: Int?,
    var coords: Position,
    var player: Player?,
    var label: String? = null
) {
    fun draw(ctx: CanvasRenderingContext2D) {
        ctx.beginPath()
        ctx.arc(this.coords.x, this.coords.y, SLOT_RADIUS, 0.0, 2 * PI)
        ctx.stroke()
        if (this.label != null) {
            ctx.font = DEFAULT_FONT
            ctx.fillStyle = determineColor(player)
            ctx.fillText(this.label!!, this.coords.x - SLOT_RADIUS / 2, this.coords.y + SLOT_RADIUS / 2)
        }
    }

    fun intercepts(position: Position): Boolean {
        return (position.x - coords.x) * (position.x - coords.x) +
                (position.y - coords.y) * (position.y - coords.y) <
                SLOT_RADIUS * SLOT_RADIUS
    }

    fun determineColor(player: Player?): String {
        return when (player) {
            null -> DEFAULT_COLOR
            else -> {
                when {
                    player.libero -> LIBERO_COLOR
                    player.teamName == TeamName.TEAM_A -> TEAM_A_COLOR
                    else -> TEAM_B_COLOR
                }
            }
        }
    }
}
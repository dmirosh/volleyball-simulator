package game.view

import game.*
import kotlinx.html.classes
import kotlinx.html.js.*
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.events.MouseEvent
import react.*
import react.dom.button
import react.dom.canvas
import react.dom.div
import react.dom.findDOMNode

fun RBuilder.game(handler: GameProps.() -> Unit): ReactElement {
    return child(GameComponent::class) {
        this.attrs(handler)
    }
}

external interface GameProps : RProps {
    var game: Game
    var fieldImage: HTMLImageElement
    var onSuccessfulTurn: () -> Unit
    var onFailedTurn: () -> Unit
    var onSwitchTeam: () -> Unit
    var onPlayerReturnToBench: (Player) -> Unit
    var onPlayerMoveToField: (Player) -> Unit
    var onPlayerOnPositionChanged: (Player, Int, Player?) -> Unit
    var onLiberoMoveToField: (Player, Player?) -> Unit
}

external interface GameState : RState {
    var canvas: Element?
    var teams: Map<TeamName, TeamView>
    var draggingPlayer: PlayerSlot?
}

const val FIELD_WIDTH = 300.0
const val FIELD_HEIGHT = 480.0

const val CANVAS_WIDTH = FIELD_WIDTH * 2 + 100
const val CANVAS_HEIGHT = FIELD_HEIGHT

const val SLOT_RADIUS = 20.0

data class Position(val x: Double, val y: Double)

class GameComponent : RComponent<GameProps, GameState>() {
    override fun componentDidMount() {
        draw()
    }

    override fun componentDidUpdate(prevProps: GameProps, prevState: GameState, snapshot: Any) {
        draw()
    }

    private fun clear(ctx: CanvasRenderingContext2D) {
        ctx.clearRect(0.0, 0.0, CANVAS_WIDTH, CANVAS_HEIGHT)
    }

    private fun draw() {
        val ctx = (state.canvas as HTMLCanvasElement).getContext("2d") as CanvasRenderingContext2D
        clear(ctx)
        drawField(ctx)
        state.teams.values.forEach { it.draw(ctx) }
        state.draggingPlayer?.draw(ctx)
    }

    private fun drawField(ctx: CanvasRenderingContext2D) {
        ctx.drawImage(
            props.fieldImage, 0.0, 0.0,
            FIELD_WIDTH,
            FIELD_HEIGHT
        )
        ctx.fillStyle = "red"
        if (props.game.currentTeam == TeamName.TEAM_A) {
            ctx.fillRect(0.0, 0.0, SLOT_RADIUS, SLOT_RADIUS)
        } else {
            ctx.fillRect(
                0.0, FIELD_HEIGHT - SLOT_RADIUS,
                SLOT_RADIUS,
                SLOT_RADIUS
            )
        }
    }

    private fun onMouseDown(event: MouseEvent) {
        val clickedPosition = Position(event.offsetX, event.offsetY)
        state.teams.values.forEach {
            val selectedBenchPlayer = it.benchPlayerWithPosition(clickedPosition)
            if (selectedBenchPlayer != null) {
                setState {
                    draggingPlayer = selectedBenchPlayer.copy(coords = clickedPosition)
                }
                return
            }
        }
    }

    private fun onMouseUp(event: MouseEvent) {
        val clickedPosition = Position(event.offsetX, event.offsetY)
        val draggingPlayer = state.draggingPlayer
        setState {
            this.draggingPlayer = null
        }
        state.teams.values.forEach {
            val selectedFieldPlayer = it.fieldPlayerWithPosition(clickedPosition)
            if (selectedFieldPlayer != null) {
                if (draggingPlayer == null || draggingPlayer.player == selectedFieldPlayer.player) {
                    props.onPlayerReturnToBench(selectedFieldPlayer.player!!)
                } else {
                    if (selectedFieldPlayer.position == LIBERO_POSITION) {
                        props.onLiberoMoveToField(draggingPlayer.player!!, selectedFieldPlayer.player)
                    } else {
                        props.onPlayerOnPositionChanged(
                            draggingPlayer.player!!,
                            selectedFieldPlayer.position!!,
                            selectedFieldPlayer.player
                        )
                    }
                }
                return
            }

            val selectedBenchPlayer = it.benchPlayerWithPosition(clickedPosition)
            if (selectedBenchPlayer != null && (draggingPlayer == null || draggingPlayer.player == selectedBenchPlayer.player)) {
                props.onPlayerMoveToField(selectedBenchPlayer.player!!)
            }
        }
    }

    private fun onMouseMove(event: MouseEvent) {
        val clickedPosition = Position(event.offsetX, event.offsetY)
        if (state.draggingPlayer != null) {
            setState {
                state.draggingPlayer!!.coords = clickedPosition
                draggingPlayer = draggingPlayer
            }
        }
    }

    override fun RBuilder.render() {
        div {
            attrs.classes = setOf("block")
            div {
                button {
                    attrs.onClickFunction = { props.onSwitchTeam() }
                    +"Сменить команду"
                }
                button {
                    attrs.onClickFunction = { props.onSuccessfulTurn() }
                    +"Подать успешно"
                }
                button {
                    attrs.onClickFunction = { props.onFailedTurn() }
                    +"Подать неуспешно"
                }
            }
            canvas {
                attrs {
                    width = "$CANVAS_WIDTH"
                    height = "$CANVAS_HEIGHT"
                    onMouseDownFunction = {
                        onMouseDown(it.asDynamic().nativeEvent as MouseEvent)
                    }
                    onMouseUpFunction = {
                        onMouseUp(it.asDynamic().nativeEvent as MouseEvent)
                    }
                    onMouseMoveFunction = {
                        onMouseMove(it.asDynamic().nativeEvent as MouseEvent)
                    }
                }
                ref { state.canvas = findDOMNode(it) }
            }
        }
    }

    companion object : RStatics<GameProps, GameState, GameComponent, Nothing>(
        GameComponent::class
    ) {
        init {
            getDerivedStateFromProps = { props, state ->
                state.teams = TeamName.values().associate {
                    it to TeamView(
                        fieldPlayers = initFieldPlayers(it, props),
                        benchPlayers = initBenchPlayers(it, props)
                    )
                }
                state
            }
        }
    }
}

fun initFieldPlayers(team: TeamName, props: GameProps): List<PlayerSlot> {
    val commonPlayers = (0 until PLAYERS_ON_FIELD_COUNT).map {
        val position = CoordsManager.mapIndexToPositionOnField(team, it)
        val player = props.game.playerOnPosition(team, position)
        PlayerSlot(
            position = position,
            coords = CoordsManager.getFieldPlayerCoords(team, it),
            player = player,
            label = player?.name
        )
    }

    val playerOnLiberoPosition = props.game.playerOnPosition(team, LIBERO_POSITION)
    val libero = PlayerSlot(
        position = LIBERO_POSITION,
        coords = CoordsManager.getFieldPlayerCoords(team, LIBERO_POSITION),
        player = playerOnLiberoPosition,
        label = playerOnLiberoPosition?.name
    )
    return commonPlayers + libero
}


private fun initBenchPlayers(team: TeamName, props: GameProps): List<PlayerSlot> {
    return props.game.teamPlayers(team).mapIndexed { index, player ->
        PlayerSlot(
            position = null,
            coords = CoordsManager.getBenchPlayerCoords(team, index),
            player = player,
            label = if (player.positionOnField == null) player.name else null
        )
    }
}


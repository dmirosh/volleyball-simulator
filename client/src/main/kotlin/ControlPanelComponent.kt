import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.div
import react.dom.span

fun RBuilder.controlPanel(handler: ControlPanelProps.() -> Unit): ReactElement {
    return child(ControlPanelComponent::class) {
        this.attrs(handler)
    }
}

external interface ControlPanelProps : RProps {
    var games: List<SavedGame>

    var onLoadGames: () -> Unit
    var onSaveGame: () -> Unit
    var onGoToGame: (Int) -> Unit
    var onDeleteGame: (Int) -> Unit
}

class ControlPanelComponent : RComponent<ControlPanelProps, RState>() {
    override fun RBuilder.render() {
        div {
            attrs.classes = setOf("block")
            div {
                button {
                    attrs.onClickFunction = { props.onLoadGames() }
                    +"Показать все игры"
                }
            }
            button {
                attrs.onClickFunction = {
                    props.onSaveGame()
                }
                +"Сохранить игру"
            }
            props.games.forEachIndexed { _, savedGame ->
                val currentGameMarker = if (savedGame.current) ">" else ""
                div {
                    span {
                        +"$currentGameMarker ${savedGame.name}"
                    }
                    button {
                        attrs.onClickFunction = { props.onGoToGame(savedGame.id) }
                        +"Загрузить игру"
                    }
                    button {
                        attrs.onClickFunction = { props.onDeleteGame(savedGame.id) }
                        +"Удалить игру"
                    }
                }

            }
        }
    }
}
package game.view

import game.Score
import kotlinx.html.classes
import react.*
import react.dom.div
import react.dom.h3

fun RBuilder.score(handler: ScoreProps.() -> Unit): ReactElement {
    return child(ScoreComponent::class) {
        this.attrs(handler)
    }
}

external interface ScoreProps : RProps {
    var score: Score
}

class ScoreComponent : RComponent<ScoreProps, RState>() {
    override fun RBuilder.render() {
        div {
            attrs.classes = setOf("block", "scoreWrapper")
            h3 {
                +"Счет: ${props.score.teamA} : ${props.score.teamB}"
            }
        }
    }
}
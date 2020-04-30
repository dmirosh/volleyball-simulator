package note

import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.div
import react.dom.textArea

fun RBuilder.notesList(handler: NotesProps.() -> Unit): ReactElement {
    return child(NotesComponent::class) {
        this.attrs(handler)
    }
}

external interface NotesProps : RProps {
    var notes: List<Note>

    var onAddNote: (String) -> Unit
}

external interface NotesState : RState {
    var currentNote: String

}

class NotesComponent : RComponent<NotesProps, NotesState>() {

    override fun NotesState.init() {
        currentNote = "Новая заметка"
    }

    override fun RBuilder.render() {
        div {
            attrs.classes = setOf("block")
            div { +"Заметки:" }
            textArea {
            }
        }
        button {
            attrs.onClickFunction = {
                val newNote = state.currentNote
                props.onAddNote(newNote)
            }
            +"Новая заметка"
        }
        div {
            for (note in props.notes) {
                div { +note.content }
            }
        }
    }
}
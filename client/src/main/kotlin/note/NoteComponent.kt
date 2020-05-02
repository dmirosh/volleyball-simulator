package note

import kotlinx.html.classes
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*

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
        currentNote = ""
    }

    override fun RBuilder.render() {
        div {
            attrs.classes = setOf("block", "notesWrapper")
            div { +"Заметки:" }
            div {
                textArea(cols = "80", rows = "10") {
                    attrs {
                        value = state.currentNote
                        onChangeFunction = {
                            handleChange(it)
                        }
                    }
                }
            }
            button {
                attrs.onClickFunction = {
                    val newNote = state.currentNote
                    setState { currentNote = "" }
                    if (newNote.trim().isNotEmpty()) {
                        props.onAddNote(newNote)
                    }
                }
                +"Новая заметка"
            }
            div {
                attrs.classes = setOf("notesList")
                for (note in props.notes) {
                    div { +note.content }
                }
            }
        }
    }

    private fun handleChange(event: Event) {
        val textArea = event.target as? HTMLTextAreaElement ?: return
        setState {
            currentNote = textArea.value
        }
    }
}
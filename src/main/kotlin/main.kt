import kotlinx.serialization.ImplicitReflectionSerializer
import react.dom.render
import kotlin.browser.document


@ImplicitReflectionSerializer
fun main() {
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}

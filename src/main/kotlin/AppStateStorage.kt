import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify

@UnstableDefault
object AppStateStorage {
    val json = Json { prettyPrint = true }

    @ImplicitReflectionSerializer
    fun serialize(appState: AppState): String {
        return json.stringify(appState)
    }

}
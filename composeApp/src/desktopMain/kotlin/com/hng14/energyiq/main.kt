import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.hng14.energyiq.App

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(width = 400.dp, height = 800.dp))
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Kotlin Starter",
        content = { App() },
    )
}

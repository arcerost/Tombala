package extrydev.app.tombala.util

import androidx.compose.runtime.compositionLocalOf
import extrydev.app.tombala.websocket.WebSocketAppState

val LocalWebSocketAppState = compositionLocalOf<WebSocketAppState> { error("No WebSocketAppState provided!") }
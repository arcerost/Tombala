package extrydev.app.tombalam.util

import androidx.compose.runtime.compositionLocalOf
import extrydev.app.tombalam.websocket.WebSocketAppState

val LocalWebSocketAppState = compositionLocalOf<WebSocketAppState> { error("No WebSocketAppState provided!") }
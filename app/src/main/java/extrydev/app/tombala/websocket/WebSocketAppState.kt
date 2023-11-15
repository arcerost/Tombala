package extrydev.app.tombala.websocket

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import extrydev.app.tombala.util.Constants.WEB_SOCKET_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class WebSocketAppState {
    val webSocket : WebSocket
    val listener: WebSocketListener
    val activeUserState : MutableState<Int> = mutableIntStateOf(0)
    private val client = OkHttpClient()
    private val req = Request.Builder().url(WEB_SOCKET_BASE_URL).build()
    init {
        listener = WebSocketListener(activeUserState, client)
        webSocket = client.newWebSocket(req, listener)
    }
}
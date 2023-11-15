package extrydev.app.tombala.websocket

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import extrydev.app.tombala.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import org.json.JSONObject

class WebSocketListener(private val activeUserState: MutableState<Int>, private val client: OkHttpClient) : okhttp3.WebSocketListener() {

    @SuppressLint("AutoboxingStateCreation")
    var receivedNumber = mutableStateOf(0)
    val receivedMessages: MutableState<List<ChatMessage>> = mutableStateOf(emptyList())
    val drawnNumbers: MutableState<List<Int>> = mutableStateOf(listOf())
    val firstLineWinnerList: MutableStateFlow<List<CardId>> = MutableStateFlow(emptyList())
    val firstLineWinnerListNames: MutableStateFlow<List<Winners>> = MutableStateFlow(emptyList())
    val secondLineWinnerList: MutableStateFlow<List<CardId>> = MutableStateFlow(emptyList())
    val secondLineWinnerListNames: MutableStateFlow<List<Winners>> = MutableStateFlow(emptyList())
    val thirdLineWinnerList: MutableStateFlow<List<CardId>> = MutableStateFlow(emptyList())
    val thirdLineWinnerListNames: MutableStateFlow<List<Winners>> = MutableStateFlow(emptyList())
    var firstLineTriggered = MutableStateFlow(false)
    var secondLineTriggered = MutableStateFlow(false)
    var thirdLineTriggered = MutableStateFlow(false)
    var sellCardTriggered = MutableStateFlow(false)
    var errorTriggered = MutableStateFlow(false)
    var errorMessage = MutableStateFlow("")
    val soldCardsState: MutableStateFlow<Map<String, UserCardInfo>> = MutableStateFlow(emptyMap())
    val userIdFromSocket = MutableStateFlow("")
    val roomDeleted = MutableStateFlow(false)
    val roomNeedRefresh = MutableStateFlow(false)
    var failControl = MutableStateFlow(false)
    private val listenerScope = CoroutineScope(Dispatchers.IO + Job())
    private var retryCount = 0
    private val maxRetryCount = 3
    private val retryDelay = 5000L

    private fun reconnect() {
        outPut("reconnecting..")
        listenerScope.launch {
            delay(retryDelay)
            val request = Request.Builder().url(Constants.WEB_SOCKET_BASE_URL).build()
            client.newWebSocket(request, this@WebSocketListener)
            retryCount++
            outPut("$retryCount reconnect try")
            failControl.value = false
        }
    }

    fun reset() {
        outPut("all reset")
        receivedNumber.value = 0
        drawnNumbers.value = emptyList()
        firstLineWinnerList.value = emptyList()
        firstLineWinnerListNames.value = emptyList()
        secondLineWinnerList.value = emptyList()
        secondLineWinnerListNames.value = emptyList()
        thirdLineWinnerList.value = emptyList()
        thirdLineWinnerListNames.value = emptyList()
        firstLineTriggered.value = false
        secondLineTriggered.value = false
        thirdLineTriggered.value = false
        sellCardTriggered.value = false
        errorTriggered.value = false
        errorMessage.value = ""
        soldCardsState.value = emptyMap()
        userIdFromSocket.value = ""
        roomDeleted.value = false
        roomNeedRefresh.value = false
    }
    override fun onOpen(webSocket: WebSocket, response: Response) {
        outPut("Connected")
        super.onOpen(webSocket, response)
    }

    fun assignUser(webSocket: WebSocket, userId: String, roomId: String){
        val paramsJson = JSONObject().apply {
            put("userId", userId)
            put("roomId", roomId)
        }
        val mainJson = JSONObject().apply {
            put("action", "assignUser")
            put("params", paramsJson)
        }
        val jsonString = mainJson.toString()
        webSocket.send(jsonString)
        outPut("AssignUser: $jsonString")
    }

    fun sendMessage(webSocket: WebSocket, roomId: String, message: String) {
        val paramsJson = JSONObject().apply {
            put("message", message)
            put("roomId", roomId)
        }

        val mainJson = JSONObject().apply {
            put("action", "onMessage")
            put("params", paramsJson)
        }

        val jsonString = mainJson.toString()
        webSocket.send(jsonString)
        outPut("Message sent: $jsonString")
    }
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("AutoboxingStateValueProperty")
    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val jsonObject = JSONObject(text)
        if (jsonObject.has("activeUser")) {
            val activeUserValue = jsonObject.getInt("activeUser")
            activeUserState.value = activeUserValue
        }
        if (jsonObject.has("roomId") && jsonObject.has("message")) {
            val receivedRoomId = jsonObject.getString("roomId")
            val receivedMessage = jsonObject.getString("message")
            val receivedUserId = jsonObject.getString("userId")
            val receivedUsername = jsonObject.getString("username")
            val receivedProfileImage = if (jsonObject.has("profileImage")) { jsonObject.getString("profileImage") } else { null }
            val receivedMessageId = jsonObject.getString("messageId")
            val chatMessage = ChatMessage(receivedMessageId, receivedUserId, receivedUsername, receivedProfileImage, receivedMessage)
            receivedMessages.value = receivedMessages.value + chatMessage

            outPut("Received message from room $receivedRoomId: $receivedMessage")
        }
        if(jsonObject.has("action") && jsonObject.has("onSell")){
            val ons = jsonObject.getString("onSell")
            outPut("ons $ons")
        }
        if (jsonObject.has("action") && jsonObject.getString("action") == "onSell") {
            if (jsonObject.getString("isSelled") == "yes") {
                val cardId = jsonObject.getString("cardId")
                val userId = jsonObject.getString("userId")
                val userCardInfo = UserCardInfo(username = jsonObject.getString("username"),
                    profileImage = if (jsonObject.has("profileImage")) {
                        jsonObject.getString("profileImage")
                    } else { null })
                soldCardsState.value = soldCardsState.value + (cardId to userCardInfo)
                userIdFromSocket.value = userId
                sellCardTriggered.value = true
                GlobalScope.launch(Dispatchers.Main) {
                    delay(1000)
                    sellCardTriggered.value = false
                }
                outPut("sold: $jsonObject")
            }
            else{
                outPut("sell: $jsonObject")
            }
        }
        if (jsonObject.has("error")) {
            val error = jsonObject.getString("error")
            val errorText = jsonObject.getString("errorText")
            errorTriggered.value = true
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000)
                errorTriggered.value = false
            }
            errorMessage.value = errorText
            outPut("hata: $error + $errorText")
        }

        if (jsonObject.has("action") && jsonObject.getString("action") == "drawnNumbers"){
            CoroutineScope(Dispatchers.Main).launch {
                receivedNumber.value = jsonObject.getInt("number")
                drawnNumbers.value = drawnNumbers.value + receivedNumber.value
            }
            outPut("$jsonObject")
        }
        if (jsonObject.has("action") && jsonObject.getString("action") == "firstLineWinners"){
            outPut("firstLineWinners")
            val cardIdArray = jsonObject.getJSONArray("cardIdArray")
            val firstWinners = jsonObject.getJSONArray("winners")
            val cardIdList = mutableListOf<CardId>()
            for (i in 0 until cardIdArray.length()) {
                cardIdList.add(CardId(cardIdArray.getString(i)))
            }
            val firstWinnersList = mutableListOf<Winners>()
            for(i in 0 until firstWinners.length()){
                firstWinnersList.add(Winners(firstWinners.getString(i)))
                outPut("winner = " + firstWinnersList[i].winners)
            }
            firstLineWinnerList.value = cardIdList
            firstLineWinnerListNames.value = firstWinnersList
            firstLineTriggered.value = true
        }
        if (jsonObject.has("action") && jsonObject.getString("action") == "secondLineWinners"){
            outPut("secondLineWinners")
            val cardIdArray = jsonObject.getJSONArray("cardIdArray")
            val secondWinners = jsonObject.getJSONArray("winners")
            val secondWinnersList = mutableListOf<Winners>()
            for(i in 0 until secondWinners.length()){
                secondWinnersList.add(Winners(secondWinners.getString(i)))
                outPut("winner = " + secondWinnersList[i].winners)
            }
            val cardIdList = mutableListOf<CardId>()
            for (i in 0 until cardIdArray.length()) {
                cardIdList.add(CardId(cardIdArray.getString(i)))
            }
            secondLineWinnerList.value = cardIdList
            secondLineWinnerListNames.value = secondWinnersList
            secondLineTriggered.value = true
        }
        if (jsonObject.has("action") && jsonObject.getString("action") == "bingoWinners"){
            outPut("bingoWinners")
            val cardIdArray = jsonObject.getJSONArray("cardIdArray")
            val bingoWinners = jsonObject.getJSONArray("winners")

            val bingoWinnersList = mutableListOf<Winners>()
            for(i in 0 until bingoWinners.length()){
                bingoWinnersList.add(Winners(bingoWinners.getString(i)))
                outPut("winner = " + bingoWinnersList[i].winners)
            }
            val cardIdList = mutableListOf<CardId>()
            for (i in 0 until cardIdArray.length()) {
                cardIdList.add(CardId(cardIdArray.getString(i)))
            }
            thirdLineWinnerList.value = cardIdList
            thirdLineWinnerListNames.value = bingoWinnersList
            thirdLineTriggered.value = true
        }
        if (jsonObject.has("action") && jsonObject.getString("action") == "roomDeleted"){
            roomDeleted.value = true
        }
        if (jsonObject.has("action") && jsonObject.getString("action") == "roomNeedRefresh"){
            roomNeedRefresh.value = true
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSE_STATUS,null)
        outPut("Closing : kapanıyor $code / $reason")
    }

    fun onSell(webSocket: WebSocket, roomId: String, cardId: String){
        val paramsJson = JSONObject().apply {
            put("cardId", cardId)
            put("roomId", roomId)
        }
        val mainJson = JSONObject().apply {
            put("action", "onSell")
            put("params", paramsJson)
        }
        val jsonString = mainJson.toString()
        webSocket.send(jsonString)
        outPut("onSell sent: $jsonString")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        failControl.value = true
        outPut("Error: hata ${t.message} ")
        if (retryCount < maxRetryCount) {
            reconnect()
        } else {
            outPut("Maximum retry count reached.")
        }
    }

    companion object{
        const val NORMAL_CLOSE_STATUS = 1000
    }
}

private fun outPut(text: String)
{
    Log.d("tombalaWebSocket",text)
}

data class ChatMessage(
    val messageId: String,
    val userId: String,
    val username: String,
    val profileImage: String?,
    val message: String
)

data class CardId(val cardId: String)

data class Winners(val winners: String)

data class UserCardInfo(val username: String, val profileImage: String?)

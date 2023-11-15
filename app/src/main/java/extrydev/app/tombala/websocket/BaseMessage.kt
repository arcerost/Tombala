package extrydev.app.tombala.websocket

interface BaseMessage {
    val roomId : String
    val message : String
    val userId : String
    val username : String
    val profileImage : String
    val messageId : String
}
package extrydev.app.tombala.model

data class RoomListResponse(
    val error: String,
    val errorText: String,
    val response: List<RoomListDetailList>
)
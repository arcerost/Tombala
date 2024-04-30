package extrydev.app.tombalam.model

data class RoomDetailResponseCard(
    val bgColor: RoomDetailResponseBgColor,
    val cardId: String,
    val constant: String,
    val firstCinkoWinner: Int,
    val firstLine: List<Int>,
    val isSelled: String,
    val roomId: String,
    val secondLine: List<Int>,
    val textColor: TextColor,
    val thirdLine: List<Int>,
    val cardPrice: Int,
    val profileImage: String?,
    val userId: String,
    val username: String
    )
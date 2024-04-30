package extrydev.app.tombalam.model

data class RoomDetailResponseDetail(
    val activeUser: Int,
    val bingoPrice: Int,
    val cards: List<RoomDetailResponseCard>,
    val constant: String,
    val createDate: String,
    val firstLinePrice: Int,
    val gameSpeed: Int,
    val isPremium: String,
    val maxCard: Int,
    val maxUser: Int,
    val repeatTime: Int,
    val roomId: String,
    val roomName: String,
    val roomStatus: String,
    val secondLinePrice: Int,
    val totalPrice: Int,
    val oldDrawnNumbers : List<Int>?
)
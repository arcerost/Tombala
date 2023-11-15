package extrydev.app.tombala.model

data class UserInfoResponse(
    val error: String,
    val errorText: String,
    val response: UserInfoResponseDetail
)
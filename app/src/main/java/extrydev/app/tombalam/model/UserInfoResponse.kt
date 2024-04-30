package extrydev.app.tombalam.model

data class UserInfoResponse(
    val error: String,
    val errorText: String,
    val response: UserInfoResponseDetail
)
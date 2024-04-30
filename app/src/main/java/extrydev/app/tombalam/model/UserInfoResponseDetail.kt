package extrydev.app.tombalam.model

data class UserInfoResponseDetail(
    val coins: Int,
    val constant: String,
    val createDate: String,
    val email: String,
    val phoneNumber: String,
    val rockets: Int,
    val userId: String,
    val username: String,
    val wallet: Int,
    val walletCurrency: String,
    val profileImage: String?
)
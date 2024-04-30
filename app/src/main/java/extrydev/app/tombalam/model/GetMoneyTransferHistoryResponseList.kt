package extrydev.app.tombalam.model

data class GetMoneyTransferHistoryResponseList(
    val action: String,
    val constant: String,
    val createDate: String,
    val description: String,
    val email: String,
    val paymentMethodId: String,
    val paymentMethodName: String,
    val phoneNumber: String,
    val price: Double,
    val receipt: String,
    val transferId: String,
    val transferStatus: String,
    val userId: String,
    val username: String
)
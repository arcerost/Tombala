package extrydev.app.tombalam.model

data class GetMoneyTransferHistoryResponse(
    val error: String,
    val errorText: String,
    val response: List<GetMoneyTransferHistoryResponseList>
)
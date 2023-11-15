package extrydev.app.tombala.model

data class ConfigResponsePayment(
    val constant: String,
    val paymentMethodId: String,
    val paymentMethodType: String,
    val pmAdres: String,
    val pmImage: String,
    val pmName: String
)
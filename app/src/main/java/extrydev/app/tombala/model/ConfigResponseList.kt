package extrydev.app.tombala.model

data class ConfigResponseList(val constant: String, val en: ConfigResponseLangEn, val tr: ConfigResponseLangTr, val coinPerMoney: Int, val ads: List<Ad>, val dailyWheel: List<DailyWheelItems>, val terms: List<String>)
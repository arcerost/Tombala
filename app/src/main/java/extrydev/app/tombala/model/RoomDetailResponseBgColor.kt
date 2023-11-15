package extrydev.app.tombala.model

import androidx.compose.ui.graphics.Color

data class RoomDetailResponseBgColor(val b: Int, val g: Int, val r: Int){
    fun toComposeColor(): Color = Color(r, g, b)

}
package extrydev.app.tombalam.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @ColumnInfo(name = "jwtToken") var jwtToken: String?,
    @ColumnInfo(name = "refreshToken") var refreshToken: String?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

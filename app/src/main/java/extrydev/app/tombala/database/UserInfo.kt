package extrydev.app.tombala.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken

@Entity
data class UserInfo(
    @ColumnInfo(name = "jwtToken") var jwtToken: String?,
    @ColumnInfo(name = "refreshToken") var refreshToken: String?,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

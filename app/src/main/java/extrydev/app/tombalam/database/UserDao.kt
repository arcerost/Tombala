package extrydev.app.tombalam.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT * FROM UserInfo")
    suspend fun getUser(): UserInfo

    @Query("SELECT COUNT(*) FROM UserInfo")
    suspend fun anyData(): Int

    @Insert
    suspend fun insert(user: UserInfo)

    @Delete
    suspend fun delete(user: UserInfo)

    @Update
    suspend fun update(user: UserInfo)

    @Query("DELETE FROM UserInfo")
    suspend fun deleteAll()
}
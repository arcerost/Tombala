package extrydev.app.tombala.service

import android.util.Log
import extrydev.app.tombala.database.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val userDao: UserDao) {

    @Volatile
    private var jwtToken: String? = null
    private var refreshToken: String? = null

    suspend fun getToken(): String? {
        withContext(Dispatchers.IO){
            if (jwtToken == null) {
                val anyData = userDao.anyData()
                if(anyData != 0)
                {
                    val user = userDao.getUser()
                    jwtToken = user.jwtToken
                }
                else
                {
                    Log.d("tombala","data yok")
                }
            }
        }
        return jwtToken
    }

    suspend fun getRefreshToken(): String? {
        withContext(Dispatchers.IO){
            if (refreshToken == null){
                val anyData = userDao.anyData()
                if(anyData != 0)
                {
                    val user = userDao.getUser()
                    user?.let {
                        refreshToken = user.refreshToken
                    }
                }
            }
        }
        return refreshToken
    }

    fun updateToken(newToken: String) {
        this.jwtToken = newToken
        runBlocking {
            val anyData = userDao.anyData()
            if(anyData != 0)
            {
                val userInfo = userDao.getUser()
                userInfo.jwtToken = newToken
                userDao.update(userInfo)
            }
        }
    }
}

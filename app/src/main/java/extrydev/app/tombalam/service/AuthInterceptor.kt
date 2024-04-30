package extrydev.app.tombalam.service

import android.content.Context
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.regions.Regions
import extrydev.app.tombalam.util.Constants.CLIENT_ID
import extrydev.app.tombalam.util.Constants.USER_POOL_ID
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(private val context: Context, private val tokenManager: TokenManager) : Interceptor {
    private fun getPhoneNumber(): String? {
        val sharedPreferences = context.getSharedPreferences("UserIdPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("phoneNumber",null)
    }
    @Volatile
    private var refreshToken: String? = null

    fun updateToken(jwtToken: String, refreshToken: String) {
        tokenManager.updateToken(jwtToken)
        this.refreshToken = refreshToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = runBlocking { tokenManager.getToken() }
        refreshToken = runBlocking { tokenManager.getRefreshToken() }
        if(token == null)
        {
            Log.d("tombala","token null")
        }
        else if (isTokenExpired(token)) {
            Log.d("tombala","expired: $token")
            val newToken = refreshToken(refreshToken)
            if (newToken != null) {
                runBlocking {
                    tokenManager.updateToken(newToken)
                    val url = request.url
                    Log.d("tombala","yeni token $newToken")
                    request = request.newBuilder()
                        .url(url)
                        .addHeader("Authorization", "$newToken")
                        .build()
                }
            }
        }
        else {
            val url = request.url
            Log.d("tombala","eski token $token")
            request = request.newBuilder()
                .url(url)
                .addHeader("Authorization", "$token")
                .build()
        }

        return chain.proceed(request)
    }
    private fun isTokenExpired(token: String?): Boolean {
        if (token == null) return true
        try {
            val jwtClaims = JwtConsumerBuilder()
                .setSkipSignatureVerification()
                .build()
                .processToClaims(token)

            val expirationTime = jwtClaims.expirationTime
            val expirationTimeMillis = expirationTime.valueInMillis
            val currentTime = System.currentTimeMillis() / 1000
            return currentTime > expirationTimeMillis


        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }
    private fun refreshToken(refreshToken: String?): String? {
        if (refreshToken == null)
        {
            return null
        }
        val latch = CountDownLatch(1)
        val userPool = CognitoUserPool(
            context,
            USER_POOL_ID,
            CLIENT_ID,
            null,
            Regions.EU_WEST_1
        )
        val cognitoUser = userPool.getUser(getPhoneNumber())
        var newToken: String? = null
        cognitoUser.getSessionInBackground(object : AuthenticationHandler {
            override fun onSuccess(
                userSession: CognitoUserSession?,
                newDevice: CognitoDevice?
            ) {
                newToken = userSession?.idToken?.jwtToken
                Log.d("tombala","yeni token refresh fonksiyonundan:  $newToken")
                latch.countDown()
            }

            override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation?,
                userId: String?
            ) {
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            }

            override fun onFailure(exception: Exception?) {
            }
        })
        latch.await()
        return newToken
    }
}


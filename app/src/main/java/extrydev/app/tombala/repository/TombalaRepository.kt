package extrydev.app.tombala.repository

import dagger.hilt.android.scopes.ActivityScoped
import extrydev.app.tombala.database.UserDao
import extrydev.app.tombala.model.CoinToTry
import extrydev.app.tombala.model.CoinToTryResponse
import extrydev.app.tombala.model.Config
import extrydev.app.tombala.model.ConfigResponse
import extrydev.app.tombala.model.ConvertMoneyToCoin
import extrydev.app.tombala.model.ConvertMoneyToCoinResponse
import extrydev.app.tombala.model.EditProfile
import extrydev.app.tombala.model.EditProfileResponse
import extrydev.app.tombala.model.GetMoneyTransferHistory
import extrydev.app.tombala.model.GetMoneyTransferHistoryResponse
import extrydev.app.tombala.model.MoneyTransfer
import extrydev.app.tombala.model.MoneyTransferResponse
import extrydev.app.tombala.model.Register
import extrydev.app.tombala.model.RegisterResponse
import extrydev.app.tombala.model.RoomDetail
import extrydev.app.tombala.model.RoomDetailResponse
import extrydev.app.tombala.model.RoomList
import extrydev.app.tombala.model.RoomListResponse
import extrydev.app.tombala.model.SnsToken
import extrydev.app.tombala.model.SnsTokenResponse
import extrydev.app.tombala.model.Support
import extrydev.app.tombala.model.SupportResponse
import extrydev.app.tombala.model.UserInfoResponse
import extrydev.app.tombala.model.Wheel
import extrydev.app.tombala.model.WheelResponse
import extrydev.app.tombala.service.TombalaApiService
import extrydev.app.tombala.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityScoped
class TombalaRepository @Inject constructor(private val api: TombalaApiService, private val userDao: UserDao) {

    // Token bilgisini almak için bir fonksiyon ekleyin
//    suspend fun getLastUser(): UserInfo {
//        return userDao.getLastUser()
//    }
//    suspend fun updateToken(user: UserInfo) {
//        userDao.update(user)
//    }

    private val _tokenFlow = MutableStateFlow("")
    val tokenFlow: Flow<String> get() = _tokenFlow

    fun saveToken(token: String) {
        _tokenFlow.value = token
    }

    suspend fun postConfig(a: String) : Resource<ConfigResponse>
    {
        val request = Config(a)
        return try {
            val response = api.postClientConfig(request)
            when(response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Config Error")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postRegisterApi(lang: String, username: String, phoneNumber: String, email: String) : Resource<RegisterResponse> {
        val request = Register(lang, username, phoneNumber, email)
        return try {
            val response = api.postClientRegisterApi(request)
            when (response.error) {
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Register Error.")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postEditProfileApi(lang: String, username: String, email: String, profileImage: String) : Resource<EditProfileResponse> {
        val request = EditProfile(lang, username, email, profileImage)
        return try {
            val response = api.postClientEditProfileApi(request)
            when (response.error) {
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Edit profile Error.")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postSupportApi(lang: String, description: String) : Resource<SupportResponse> {
        val request = Support(lang, description)
        return try {
            val response = api.postClientSupportApi(request)
            when (response.error) {
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("Support Error.")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postMoneyTransferApi(lang: String, action: String, paymentMethodId: String, price: Double, description: String?, bankName: String?, namesurname: String?, iban: String?, receipt: String?): Resource<MoneyTransferResponse> {
        val request = MoneyTransfer(lang, action, paymentMethodId, price, description, bankName, namesurname, iban, receipt)
        return try {
            val response = api.postClientMoneyTransferApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("MoneyTransferError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postUserInfoApi(lang: String): Resource<UserInfoResponse> {
        val request = extrydev.app.tombala.model.UserInfo(lang)
        return try {
            val response = api.postClientUserInfoApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("UserInfoError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postRoomListApi(lang: String): Resource<RoomListResponse> {
        val request = RoomList(lang)
        return try {
            val response = api.postClientRoomListApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("RoomListError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postRoomDetailApi(lang: String, roomId: String): Resource<RoomDetailResponse> {
        val request = RoomDetail(lang, roomId)
        return try {
            val response = api.postClientRoomDetailApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("RoomDetailError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postRoomCoinToTryApi(lang: String, coins: Int): Resource<CoinToTryResponse> {
        val request = CoinToTry(lang, coins)
        return try {
            val response = api.postClientCoinToTryApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorMessage)
                else -> Resource.Error("CoinToTryError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postRoomConvertMoneyToCoinApi(lang: String, packageId: String): Resource<ConvertMoneyToCoinResponse> {
        val request = ConvertMoneyToCoin(lang, packageId)
        return try {
            val response = api.postClientConvertMoneyToCoinApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorMessage)
                else -> Resource.Error("ConvertMoneyToCoinError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postSnsTokenApi(lang: String, appToken: String, sourcePlatform: String, appMode: String): Resource<SnsTokenResponse> {
        val request = SnsToken(lang, appToken, sourcePlatform, appMode)
        return try {
            val response = api.postClientSnsTokenApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("RoomDetailError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    suspend fun postWheelApi(lang: String, price: Int): Resource<WheelResponse> {
        val request = Wheel(lang, price)
        return try {
            val response = api.postClientWheelApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("WheelError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }


    suspend fun postGetMoneyTransferHistoryApi(lang: String): Resource<GetMoneyTransferHistoryResponse> {
        val request = GetMoneyTransferHistory(lang)
        return try {
            val response = api.postClientGetMoneyTransferHistoryApi(request)
            when (response.error){
                "0" -> Resource.Success(response)
                "1" -> Resource.Error(response.errorText)
                else -> Resource.Error("GetMoneyTransferHistoryError")
            }
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
}
package extrydev.app.tombala.service

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
import extrydev.app.tombala.model.UserInfo
import extrydev.app.tombala.model.UserInfoResponse
import extrydev.app.tombala.model.Wheel
import extrydev.app.tombala.model.WheelResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TombalaApiService {
    @Headers("Content-Type:application/json")
    @POST("tombala-client-config")
    suspend fun postClientConfig(@Body request: Config) : ConfigResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-register")
    suspend fun postClientRegisterApi(@Body request: Register) : RegisterResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-editprofile")
    suspend fun postClientEditProfileApi(@Body request: EditProfile) : EditProfileResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-support")
    suspend fun postClientSupportApi(@Body request: Support) : SupportResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-moneytransfer")
    suspend fun postClientMoneyTransferApi(@Body request: MoneyTransfer) : MoneyTransferResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-userinfo")
    suspend fun postClientUserInfoApi(@Body request: UserInfo) : UserInfoResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-roomlist")
    suspend fun postClientRoomListApi(@Body request: RoomList) : RoomListResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-roomdetail")
    suspend fun postClientRoomDetailApi(@Body request: RoomDetail) : RoomDetailResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-cointotry")
    suspend fun postClientCoinToTryApi(@Body request: CoinToTry) : CoinToTryResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-convertmoneytocoin")
    suspend fun postClientConvertMoneyToCoinApi(@Body request: ConvertMoneyToCoin) : ConvertMoneyToCoinResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-addsnstoken")
    suspend fun postClientSnsTokenApi(@Body request: SnsToken) : SnsTokenResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-wheel")
    suspend fun postClientWheelApi(@Body request: Wheel) : WheelResponse

    @Headers("Content-Type:application/json")
    @POST("tombala-client-getmoneytransferhistory")
    suspend fun postClientGetMoneyTransferHistoryApi(@Body request: GetMoneyTransferHistory) : GetMoneyTransferHistoryResponse
}
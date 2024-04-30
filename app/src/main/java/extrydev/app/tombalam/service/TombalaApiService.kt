package extrydev.app.tombalam.service

import extrydev.app.tombalam.model.CoinToTry
import extrydev.app.tombalam.model.CoinToTryResponse
import extrydev.app.tombalam.model.Config
import extrydev.app.tombalam.model.ConfigResponse
import extrydev.app.tombalam.model.ConvertMoneyToCoin
import extrydev.app.tombalam.model.ConvertMoneyToCoinResponse
import extrydev.app.tombalam.model.EditProfile
import extrydev.app.tombalam.model.EditProfileResponse
import extrydev.app.tombalam.model.GetMoneyTransferHistory
import extrydev.app.tombalam.model.GetMoneyTransferHistoryResponse
import extrydev.app.tombalam.model.MoneyTransfer
import extrydev.app.tombalam.model.MoneyTransferResponse
import extrydev.app.tombalam.model.Register
import extrydev.app.tombalam.model.RegisterResponse
import extrydev.app.tombalam.model.RoomDetail
import extrydev.app.tombalam.model.RoomDetailResponse
import extrydev.app.tombalam.model.RoomList
import extrydev.app.tombalam.model.RoomListResponse
import extrydev.app.tombalam.model.SnsToken
import extrydev.app.tombalam.model.SnsTokenResponse
import extrydev.app.tombalam.model.Support
import extrydev.app.tombalam.model.SupportResponse
import extrydev.app.tombalam.model.UserInfo
import extrydev.app.tombalam.model.UserInfoResponse
import extrydev.app.tombalam.model.Wheel
import extrydev.app.tombalam.model.WheelResponse
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
package extrydev.app.tombala.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import extrydev.app.tombala.model.Ad
import extrydev.app.tombala.model.DailyWheelItems
import extrydev.app.tombala.model.RoomListDetailList
import extrydev.app.tombala.model.UserInfoResponseDetail
import extrydev.app.tombala.repository.TombalaRepository
import extrydev.app.tombala.util.Resource
import extrydev.app.tombala.view.findMinutesUntilNextInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TombalaRepository, @ApplicationContext private val context: Context) : ViewModel(),
     PurchasesUpdatedListener {
    val errorMessage = MutableStateFlow(String())
    val roomList = MutableStateFlow<List<RoomListDetailList>>(emptyList())
    val userInfo : MutableStateFlow<UserInfoResponseDetail?> = MutableStateFlow(null)
    private val errorMessageFromUserInfo = MutableStateFlow(String())
    private val completedCheck = MutableStateFlow(false)
    private val _timeLeftMillis = MutableLiveData<Long>()
    val timeLeftMillis: LiveData<Long> get() = _timeLeftMillis
    val keyList = MutableStateFlow<List<Ad>>(listOf())
    val dailyWheelItems = MutableStateFlow<List<DailyWheelItems>>(listOf())
    val wheelCheck = MutableStateFlow(false)

    private suspend fun getNTPTime(): Long {
        return withContext(Dispatchers.IO) {
            try {
                val client = NTPUDPClient()
                client.defaultTimeout = 10000
                client.open()

                val info = client.getTime(InetAddress.getByName("time.google.com"))
                val ntpV3Packet = info.message
                val ntpTime = ntpV3Packet.transmitTimeStamp.time

                client.close()
                ntpTime
            } catch (e: Exception) {
                e.printStackTrace()
                -1L
            }
        }
    }

    fun startCounter(interval: Int) {
        if (interval <= 0) {
            return
        }
        viewModelScope.launch {
            while (true) {
                val ntpTime = getNTPTime()
                if (ntpTime != -1L) {
                    val calendar = Calendar.getInstance().apply { timeInMillis = ntpTime }
                    val minutesUntilNextInterval =
                        findMinutesUntilNextInterval(calendar, interval)
                    var remainingMillis = TimeUnit.MINUTES.toMillis(minutesUntilNextInterval)
                    _timeLeftMillis.postValue(remainingMillis)
                    while (remainingMillis > 0) {
                        delay(1000)
                        remainingMillis -= 1000
                        _timeLeftMillis.postValue(remainingMillis)
                    }
                    val updatedNtpTime = getNTPTime()
                    if (updatedNtpTime != -1L) {
                        calendar.timeInMillis = updatedNtpTime
                    }
                } else {
                    errorMessage.value = "NTP error."
                }
            }
        }
    }

    fun getRooms(lang: String){
        viewModelScope.launch {
            when( val result = repository.postRoomListApi(lang)){
                is Resource.Success -> {
                    roomList.value = result.data!!.response
                }
                is Resource.Error -> {
                    errorMessage.value = result.message.toString()
                }
            }
        }
    }

    fun getUserInfo(lang: String){
        viewModelScope.launch {
            when(val result = repository.postUserInfoApi(lang)){
                is Resource.Success -> {
                    completedCheck.value = true
                    userInfo.value = result.data!!.response
                }
                is Resource.Error -> {
                    errorMessageFromUserInfo.value = result.message.toString()
                }
            }
        }
    }
    fun getConfig(){
        viewModelScope.launch{
            when(val result = repository.postConfig("a")){
                is Resource.Success -> {
                    keyList.value = result.data!!.response.ads
                    dailyWheelItems.value = result.data.response.dailyWheel
                }
                is Resource.Error -> {

                }
            }
        }
    }

    fun postWheel(lang: String, price: Int){
        viewModelScope.launch{
            when(val result = repository.postWheelApi(lang, price)){
                is Resource.Success -> {
                    result.data!!.response
                    wheelCheck.value = true
                }
                is Resource.Error -> {
                    Log.d("tombala", result.message.toString())
                }
            }
        }
    }

    private val _purchases = MutableStateFlow<List<Purchase>?>(null)
    val purchases: MutableStateFlow<List<Purchase>?> = _purchases
    private val billingConnectionState = MutableLiveData(false)

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        startBillingConnection(billingConnectionState)
    }
    fun startBillingConnection(billingConnectionState: MutableLiveData<Boolean>) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("tombala", "Billing response OK")
                    queryPurchases()
                    billingConnectionState.postValue(true)
                } else {
                    Log.e("tombala", billingResult.debugMessage)
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.i("tombala", "Billing connection disconnected")
                startBillingConnection(billingConnectionState)
            }
        })
    }
    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e("tombala", "queryPurchases: BillingClient is not ready")
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchaseList.isNotEmpty()) {
                    Log.e("tombala", "purchases not empty")
                    _purchases.value = purchaseList
                } else {
                    Log.e("tombala", "purchases empty")
                    _purchases.value = emptyList()
                }

            } else {
                Log.e("tombala", billingResult.debugMessage)
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
    }
}
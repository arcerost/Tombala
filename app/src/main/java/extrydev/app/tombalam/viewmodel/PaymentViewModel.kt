package extrydev.app.tombalam.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import extrydev.app.tombalam.model.ConfigResponseCoin
import extrydev.app.tombalam.model.UserInfoResponseDetail
import extrydev.app.tombalam.repository.TombalaRepository
import extrydev.app.tombalam.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(private val repository: TombalaRepository,@ApplicationContext private val context: Context) : ViewModel(), BillingClientStateListener, PurchasesUpdatedListener, ProductDetailsResponseListener {
    val userInfo: MutableStateFlow<UserInfoResponseDetail?> = MutableStateFlow(null)
    private val errorMessageFromUserInfo = MutableStateFlow(String())
    private val completedCheck = MutableStateFlow(false)
    val coins = MutableStateFlow(listOf<ConfigResponseCoin>())
    private val completedCheck2 = MutableStateFlow(false)
    private val completedCheck3 = MutableStateFlow(false)
    private val errorMessageFromSendMoney = MutableStateFlow(String())
    val aylikSubPrice = MutableStateFlow("")
    val aylikSubTitle = MutableStateFlow("")
    val yillikSubPrice = MutableStateFlow("")
    val yillikSubTitle = MutableStateFlow("")
    val gunlukSubPrice = MutableStateFlow("")
    val gunlukSubTitle = MutableStateFlow("")
    private val billingConnectionState = MutableLiveData(false)
    var changeCoinResponse = MutableStateFlow("")

    fun getConfig(lang: String) {
        viewModelScope.launch {
            when (val result = repository.postConfig(lang)) {
                is Resource.Success -> {
                    val response = result.data
                    if (response != null) {
                        coins.value = result.data.response.tr.coinPackages
                        completedCheck2.value = true
                    }
                }
                is Resource.Error -> {
                    errorMessageFromUserInfo.value = result.message.toString()
                }
            }
        }
    }

    fun getUserInfo(lang: String) {
        viewModelScope.launch {
            when (val result = repository.postUserInfoApi(lang)) {
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

    fun sendMoney(lang: String, packageId: String) {
        viewModelScope.launch {
            when (val result = repository.postRoomConvertMoneyToCoinApi(lang, packageId)) {
                is Resource.Success -> {
                    changeCoinResponse.value = result.data!!.response
                    completedCheck3.value = true
                }
                is Resource.Error -> {
                    errorMessageFromSendMoney.value = result.message.toString()
                }
            }
        }
    }

    private val _purchases = MutableStateFlow<List<Purchase>?>(null)
    val purchases: MutableStateFlow<List<Purchase>?> = _purchases

    private val listOfProducts = listOf("aylikabonelik","gunlukabonelik_","yillikabonelik_")

    private val _isNewPurchaseAcknowledged = MutableStateFlow<Boolean?>(null)
    val isNewPurchaseAcknowledged: MutableStateFlow<Boolean?> = _isNewPurchaseAcknowledged

    val  productWithProductDetails = MutableStateFlow<Map<String, ProductDetails>?>(null)

    val billingClient = BillingClient.newBuilder(context)
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
                    queryProductDetails()
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
    // alınmış ürünlerin sorgusu
    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e("tombala", "queryPurchases: BillingClient is not ready")
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
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

    fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
        val productList = mutableListOf<QueryProductDetailsParams.Product>()
        for (product in listOfProducts) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
        }
        params.setProductList(productList).let { productDetailsParams ->
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d("tombala","billing service disconnected")
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        Log.d("tombala","billing setup finished")
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
            && !purchases.isNullOrEmpty()
        ) {
            _purchases.value = purchases
            for (purchase in purchases) {
                acknowledgePurchases(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.e("tombala", "User has cancelled")
        } else {
            Log.d("tombala","purchase error..")
        }
    }

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        if (!billingClient.isReady) {
            Log.e("tombala", "launchBillingFlow: BillingClient is not ready")
        }
        else
        {
            billingClient.launchBillingFlow(activity, params)
            Log.d("tombala","floww")
        }

    }

    private fun acknowledgePurchases(purchase: Purchase?) {
        purchase?.let {
            if (!it.isAcknowledged) {
                try {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(it.purchaseToken)
                        .build()

                    billingClient.acknowledgePurchase(
                        params
                    ) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                            it.purchaseState == Purchase.PurchaseState.PURCHASED
                        ) {
                            _isNewPurchaseAcknowledged.value = true
                        }
                    }
                }
                catch (e: Exception){
                    Log.d("purchase","error: $e")
                }
            }
        }
    }

    private fun terminateBillingConnection() {
        Log.i("tombala", "Terminating connection")
        billingClient.endConnection()
    }

    override fun onProductDetailsResponse(billingResult: BillingResult, productDetailsList: MutableList<ProductDetails>) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                var newMap = emptyMap<String, ProductDetails>()
                if (productDetailsList.isEmpty()) {
                    Log.e("tombala", "onProductDetailsResponse: " +
                                "Found null or empty ProductDetails. " +
                                "Check to see if the Products you requested are correctly " +
                                "published in the Google Play Console.")
                }
                else {
                    newMap = productDetailsList.associateBy {
                        it.productId
                    }
                    newMap["aylikabonelik"]?.let {
                        aylikSubPrice.value = it.description
                        aylikSubTitle.value = it.title
                    }

                    newMap["yillikabonelik_"]?.let {
                        yillikSubPrice.value = it.description
                        yillikSubTitle.value = it.title
                    }
                    newMap["gunlukabonelik_"]?.let {
                        gunlukSubTitle.value = it.title
                        gunlukSubPrice.value = it.description
                    }
                }
                productWithProductDetails.value = newMap
            }
            else -> {
                Log.i("tombala", "onProductDetailsResponse: $responseCode $debugMessage")
            }
        }
    }
}

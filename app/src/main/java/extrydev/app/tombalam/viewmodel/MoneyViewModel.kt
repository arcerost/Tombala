package extrydev.app.tombalam.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombalam.model.ConfigResponseFaq
import extrydev.app.tombalam.model.ConfigResponsePayment
import extrydev.app.tombalam.model.UserInfoResponseDetail
import extrydev.app.tombalam.repository.TombalaRepository
import extrydev.app.tombalam.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoneyViewModel @Inject constructor(private val repository: TombalaRepository): ViewModel(){
    private val errorMessage = MutableStateFlow("")
    private val errorMessageForEditProfile = MutableStateFlow("")
    val result = MutableStateFlow("")
    val paymentMethods = MutableStateFlow(listOf<ConfigResponsePayment>())
    val selectedMethod = mutableStateOf<ConfigResponsePayment?>(null)
    private val faqs = mutableStateOf(listOf<ConfigResponseFaq>())
    val userInfo : MutableStateFlow<UserInfoResponseDetail?> = MutableStateFlow(null)
    private val errorMessageFromUserInfo = MutableStateFlow(String())
    val searchQuery = mutableStateOf("")
    val completedCheck = MutableStateFlow(false)
    val filteredFaqs = derivedStateOf {
        faqs.value.filter { it.questionTitle.contains(searchQuery.value, ignoreCase = true) }
    }
    init {
        getMethods()
    }
    fun getUserInfo(lang: String){
        viewModelScope.launch {
            when(val result = repository.postUserInfoApi(lang)){
                is Resource.Success -> {
                    userInfo.value = result.data!!.response
                    completedCheck.value = true
                }
                is Resource.Error -> {
                    errorMessageFromUserInfo.value = result.message.toString()
                }
            }
        }
    }

    private fun getMethods(){
        viewModelScope.launch {
            when(val result = repository.postConfig("tr")){
                is Resource.Success -> {
                    val response = result.data
                    if(response != null)
                    {
                        paymentMethods.value = response.response.tr.paymentMethods
                        faqs.value = response.response.tr.faq
                    }
                }
                is Resource.Error -> {
                    errorMessage.value = result.message.toString()
                }
            }
        }
    }

    fun moneyTransfer(lang: String, action: String, paymentMethodId: String, price: Double, description: String?, bankName: String?, namesurname: String?, iban: String?, receipt: String?){
        viewModelScope.launch {
            when(val resultFromApi  = repository.postMoneyTransferApi(lang, action, paymentMethodId, price, description, bankName, namesurname, iban, receipt)){
                is Resource.Success ->{
                    result.value = resultFromApi.data?.response ?: ""
                }
                is Resource.Error -> {
                    errorMessageForEditProfile.value = resultFromApi.message.toString()
                }
            }
        }
    }
}
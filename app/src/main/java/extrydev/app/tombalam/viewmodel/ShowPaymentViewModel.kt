package extrydev.app.tombalam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombalam.model.GetMoneyTransferHistoryResponseList
import extrydev.app.tombalam.model.UserInfoResponseDetail
import extrydev.app.tombalam.repository.TombalaRepository
import extrydev.app.tombalam.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowPaymentViewModel @Inject constructor(private val repository: TombalaRepository) : ViewModel() {
    val userInfo : MutableStateFlow<UserInfoResponseDetail?> = MutableStateFlow(null)
    private val errorMessageFromUserInfo = MutableStateFlow(String())
    private val completedCheck = MutableStateFlow(false)
    val historyList = MutableStateFlow<List<GetMoneyTransferHistoryResponseList>>(emptyList())
    private val errorMessageFromHistoryList = MutableStateFlow(String())
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

    fun getHistory(lang:String){
        viewModelScope.launch {
            when(val result = repository.postGetMoneyTransferHistoryApi(lang)){
                is Resource.Success -> {
                    historyList.value = result.data!!.response
                }
                is Resource.Error -> {
                    errorMessageFromHistoryList.value = result.message.toString()
                }
            }
        }
    }
}
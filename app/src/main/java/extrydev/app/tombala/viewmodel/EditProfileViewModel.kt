package extrydev.app.tombala.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombala.model.UserInfoResponseDetail
import extrydev.app.tombala.repository.TombalaRepository
import extrydev.app.tombala.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val repository: TombalaRepository) : ViewModel() {
    private val errorMessage = MutableStateFlow("")
    val result = MutableStateFlow("")
    val userInfo : MutableStateFlow<UserInfoResponseDetail?> = MutableStateFlow(null)
    val errorMessageFromUserInfo = MutableStateFlow(String())
    private val errorMessageFromConfig = MutableStateFlow(String())
    val completedCheck = MutableStateFlow(false)
    var coinPerMoney = MutableStateFlow(0)
    var changeCoinResponse = MutableStateFlow("")
    private val completedCheckFromEditProfile = MutableStateFlow(false)
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

    fun config(){
        viewModelScope.launch {
            when(val resultFromApi = repository.postConfig("a")){
                is Resource.Success -> {
                    coinPerMoney.value = resultFromApi.data!!.response.coinPerMoney
                }
                is Resource.Error -> {
                    errorMessageFromConfig.value = resultFromApi.message.toString()
                }
            }
        }
    }

    fun editProfile(lang: String, email: String, username: String, profileImage: String){
        viewModelScope.launch {
            when(val resultFromApi  = repository.postEditProfileApi(lang, username, email, profileImage)){
                is Resource.Success ->{
                    completedCheckFromEditProfile.value = true
                    result.value = resultFromApi.data?.response ?: ""
                }
                is Resource.Error -> {
                    completedCheckFromEditProfile
                    errorMessage.value = resultFromApi.message.toString()
                    Log.d("tombala", errorMessage.value)
                }
            }
        }
    }

    fun changeCoin(lang: String, coins: Int){
        viewModelScope.launch {
            when(val resultFromApi = repository.postRoomCoinToTryApi(lang, coins)){
                is Resource.Success -> {
                    changeCoinResponse.value = resultFromApi.data!!.response
                    Log.d("tombala", changeCoinResponse.value)
                }
                is Resource.Error -> {
                    errorMessageFromConfig.value = resultFromApi.message.toString()
                }
            }
        }
    }
}
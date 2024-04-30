package extrydev.app.tombalam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombalam.repository.TombalaRepository
import extrydev.app.tombalam.service.AuthInterceptor
import extrydev.app.tombalam.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel@Inject constructor(private val authInterceptor: AuthInterceptor, private val repository: TombalaRepository) : ViewModel() {
    val errorMessage = MutableStateFlow("")
    private val errorMessage2 = MutableStateFlow("")
    val completed = MutableStateFlow(false)
    private val completed2 = MutableStateFlow(false)
    val termsList = MutableStateFlow(listOf<String>())
    val tokenFlow: Flow<String> = repository.tokenFlow
    fun beRegister(lang: String, username: String, phoneNumber: String, email: String){
        viewModelScope.launch {
            when(val result = repository.postRegisterApi(lang, username, phoneNumber, email))
            {
                is Resource.Success ->{
                    completed.value = true
                }
                is Resource.Error ->{
                    completed.value = false
                    errorMessage.value = result.message.toString()
                }
            }
        }
    }
    fun setAuthToken(jwtToken: String, refreshToken: String) {
        authInterceptor.updateToken(jwtToken, refreshToken)
    }

    fun setSnsToken(lang: String, appToken: String, sourcePlatform: String, appMode: String){
        viewModelScope.launch {
            when(val result = repository.postSnsTokenApi(lang,appToken, sourcePlatform, appMode))
            {
                is Resource.Success ->{
                    completed2.value = true
                }
                is Resource.Error ->{
                    completed2.value = false
                    errorMessage2.value = result.message.toString()
                }
            }
        }
    }

    fun getConfig(){
        viewModelScope.launch{
            when(val result = repository.postConfig("a")){
                is Resource.Success -> {
                    termsList.value = result.data!!.response.terms
                }
                is Resource.Error -> {

                }
            }
        }
    }
}
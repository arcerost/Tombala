package extrydev.app.tombala.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombala.repository.TombalaRepository
import extrydev.app.tombala.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: TombalaRepository) : ViewModel() {
    val errorMessage = MutableStateFlow("")
    private val completed = MutableStateFlow(false)
    val tokenFlow: Flow<String> = repository.tokenFlow

    fun setSnsToken(lang: String, appToken: String, sourcePlatform: String, appMode: String){
        viewModelScope.launch {
            when(val result = repository.postSnsTokenApi(lang,appToken, sourcePlatform, appMode))
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
}
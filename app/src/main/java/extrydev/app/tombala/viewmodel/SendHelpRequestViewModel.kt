package extrydev.app.tombala.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombala.repository.TombalaRepository
import extrydev.app.tombala.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SendHelpRequestViewModel @Inject constructor(private val repository: TombalaRepository): ViewModel() {
    private val errorMessage = MutableStateFlow("")
    val result = MutableStateFlow("")

    fun sendHelpRequest(lang: String, description: String){
        viewModelScope.launch {
            when(val resultFromApi  = repository.postSupportApi(lang, description)){
                is Resource.Success ->{
                    result.value = resultFromApi.data?.response ?: ""
                }
                is Resource.Error -> {
                    errorMessage.value = resultFromApi.message.toString()
                }
            }
        }
    }
}
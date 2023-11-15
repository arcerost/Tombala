package extrydev.app.tombala.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.tombala.model.RoomDetailResponseDetail
import extrydev.app.tombala.model.UserInfoResponseDetail
import extrydev.app.tombala.repository.TombalaRepository
import extrydev.app.tombala.util.Presets
import extrydev.app.tombala.util.Resource
import extrydev.app.tombala.view.findMinutesUntilNextInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.dionsegijn.konfetti.core.Party
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class GameViewModel @Inject constructor(private val repository: TombalaRepository) : ViewModel() {
    val errorMessage = MutableStateFlow(String())
    val errorMessageFromRoom = MutableStateFlow(String())
    val roomDetail: MutableStateFlow<RoomDetailResponseDetail?> = MutableStateFlow(null)
    val userInfo : MutableStateFlow<UserInfoResponseDetail?> = MutableStateFlow(null)
    private val errorMessageFromUserInfo = MutableStateFlow(String())
    val completedCheckForRoom = MutableStateFlow(false)
    val completedCheck = MutableStateFlow(false)
    private val _timeLeftMillis = MutableLiveData<Long>()
    val timeLeftMillis: LiveData<Long> get() = _timeLeftMillis

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
                    val minutesUntilNextInterval = findMinutesUntilNextInterval(calendar, interval)
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


    fun getRoomDetails(lang: String, roomId : String){
        viewModelScope.launch {
            when(val result = repository.postRoomDetailApi(lang, roomId)){
                is Resource.Success -> {
                    roomDetail.value = result.data!!.response
                    completedCheckForRoom.value = true
                }
                is Resource.Error -> {
                    completedCheckForRoom.value = true
                    errorMessageFromRoom.value = result.message.toString()
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

    fun resetViewModel(){
        Log.d("tombalawebsocket","allresetvm")
        errorMessage.value = ""
        errorMessageFromRoom.value = ""
        errorMessageFromUserInfo.value = ""
        roomDetail.value = null
        userInfo.value = null
        completedCheck.value = false
        completedCheckForRoom.value = false
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state
    fun parade() {
        _state.value = State.Started(Presets.parade())
    }
    fun rain() {
        _state.value = State.Started(Presets.rain())
    }
    fun ended() {
        _state.value = State.Idle
    }
    sealed class State {
        class Started(val party: List<Party>) : State()
        object Idle : State()
    }
}
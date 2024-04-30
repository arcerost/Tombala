package extrydev.app.tombalam

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import extrydev.app.tombalam.model.ConfigResponseIntro
import extrydev.app.tombalam.repository.TombalaRepository
import extrydev.app.tombalam.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


val Context.dataStore by preferencesDataStore(name = "my_data_store")
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: TombalaRepository, @ApplicationContext private val context: Context) : ViewModel(){
    private var errorMessage by mutableStateOf("")
    private val _onboardingPages = MutableStateFlow<List<ConfigResponseIntro>>(emptyList())
    val onboardingPages: StateFlow<List<ConfigResponseIntro>> = _onboardingPages
    private val _pageIndex = MutableStateFlow(0)  // Başlangıç sayfası 0
    val pageIndex: StateFlow<Int> = _pageIndex
    fun nextPage() {
        if (_pageIndex.value < (_onboardingPages.value.size - 1)) {
            _pageIndex.value += 1
        }
    }
    suspend fun setPageIndex(index: Int) {
        _pageIndex.emit(index)
    }

    init {
        fetchOnboardingPages()
    }

    private fun fetchOnboardingPages() {
        viewModelScope.launch {
            when (val result = repository.postConfig("tr")) {
                is Resource.Success -> {
                    val response = result.data
                    if (response != null) {
                        _onboardingPages.value = response.response.tr.intro.map {
                            ConfigResponseIntro(
                                image = it.image,
                                nextBtnText = it.nextBtnText,
                                skipBtnText = it.skipBtnText,
                                text = it.text
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    errorMessage = "Hata"
                }
            }
        }
    }

    private object PreferencesKeys {
        val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    }

    private val onboardingCompleted = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[PreferencesKeys.onboardingCompletedKey] ?: false
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.onboardingCompletedKey] = true
        }
    }
    fun getStartDestination(): Flow<String> {
        return onboardingCompleted.map { onboardingCompleted ->
            when {
                !onboardingCompleted -> "false"
                else -> "true"
            }
        }
    }
}
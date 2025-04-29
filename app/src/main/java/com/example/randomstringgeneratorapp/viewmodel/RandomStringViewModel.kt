package com.example.randomstringgeneratorapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomstringgeneratorapp.data.model.RandomStringData
import com.example.randomstringgeneratorapp.data.repository.RandomStringRepository
import com.example.randomstringgeneratorapp.ui.state.RandomStringUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RandomStringViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = RandomStringRepository(application.contentResolver)
    private val _uiState = MutableStateFlow<RandomStringUiState>(RandomStringUiState.Empty)
    val uiState: StateFlow<RandomStringUiState> get() = _uiState

    fun generateRandomString(length: Int) {
        viewModelScope.launch {
            val currentList = repository.randomStrings.value
            _uiState.value = RandomStringUiState.Success(currentList, isLoading = true)

            val result = withContext(Dispatchers.IO) {
                runCatching { repository.generateRandomString(length) }
            }

            result.fold(
                onSuccess = {
                    _uiState.value = RandomStringUiState.Success(
                        repository.randomStrings.value,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value =
                        RandomStringUiState.Error(e.localizedMessage ?: "An error occurred")
                }
            )
        }
    }

    fun deleteString(randomString: RandomStringData) {
        repository.deleteString(randomString)
        _uiState.value = RandomStringUiState.Success(repository.randomStrings.value)
    }

    fun clearAllStrings() {
        repository.clearAllStrings()
        _uiState.value = RandomStringUiState.Empty
    }
}
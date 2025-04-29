package com.example.randomstringgeneratorapp.data.repository

import com.example.randomstringgeneratorapp.data.model.RandomStringData
import com.example.randomstringgeneratorapp.data.provider.RandomStringProviderHelper
import android.content.ContentResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class RandomStringRepository(private val contentResolver: ContentResolver) {

    // Backing list of generated strings
    private val _randomStrings = MutableStateFlow<List<RandomStringData>>(emptyList())
    val randomStrings: StateFlow<List<RandomStringData>> get() = _randomStrings

    /**
     * Generates a new random string by querying the content provider.
     */
    fun generateRandomString(length: Int): Result<RandomStringData> {
        return RandomStringProviderHelper.fetchRandomString(contentResolver, length)
            .onSuccess { result ->
                _randomStrings.update { currentList ->
                    currentList + result
                }
            }
    }

    /**
     * Deletes a single string from the list.
     */
    fun deleteString(item: RandomStringData) {
        _randomStrings.update { currentList ->
            currentList.filterNot { it == item }
        }
    }

    /**
     * Clears all generated strings.
     */
    fun clearAllStrings() {
        _randomStrings.value = emptyList()
    }
}

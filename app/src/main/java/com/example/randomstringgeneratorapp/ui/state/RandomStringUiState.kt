package com.example.randomstringgeneratorapp.ui.state

import com.example.randomstringgeneratorapp.data.model.RandomStringData

/**
 * Represents different UI states for the RandomStringScreen.
 */
sealed class RandomStringUiState {

    // Loading state
    data object Loading : RandomStringUiState()

    // Success state with a list of generated strings
    data class Success(val randomStrings: List<RandomStringData>, val isLoading: Boolean = false) : RandomStringUiState()

    // Error state with an error message
    data class Error(val message: String) : RandomStringUiState()

    // Empty state when no data is available
    data object Empty : RandomStringUiState()
}
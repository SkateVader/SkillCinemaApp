package com.boardgames.skillcinema.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: OnboardingRepository
) : ViewModel() {

    val isOnboardingCompleted = repository.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted()
        }
    }
}

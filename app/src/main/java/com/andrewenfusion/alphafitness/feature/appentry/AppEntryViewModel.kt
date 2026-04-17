package com.andrewenfusion.alphafitness.feature.appentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewenfusion.alphafitness.domain.usecase.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AppEntryUiState())
    val uiState: StateFlow<AppEntryUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserProfileUseCase().collect { profile ->
                mutableUiState.update {
                    AppEntryUiState(
                        isLoading = false,
                        destination = if (profile?.isSetupComplete == true) {
                            AppEntryDestination.MAIN_SHELL
                        } else {
                            AppEntryDestination.ONBOARDING
                        },
                    )
                }
            }
        }
    }
}

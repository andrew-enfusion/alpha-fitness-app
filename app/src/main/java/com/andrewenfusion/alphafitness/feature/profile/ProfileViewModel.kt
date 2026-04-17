package com.andrewenfusion.alphafitness.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewenfusion.alphafitness.domain.usecase.ObserveNutritionGuidanceUseCase
import com.andrewenfusion.alphafitness.domain.usecase.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    observeNutritionGuidanceUseCase: ObserveNutritionGuidanceUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserProfileUseCase().collect { profile ->
                if (profile == null) {
                    mutableUiState.update { ProfileUiState(isLoading = false) }
                } else {
                    mutableUiState.update { current ->
                        current.copy(
                            isLoading = false,
                            age = profile.age.toString(),
                            sex = profile.sex.name.lowercase().replaceFirstChar(Char::uppercase),
                            height = "${profile.heightCm.toInt()} cm",
                            weight = "${profile.weightKg.toInt()} kg",
                            exerciseLevel = profile.exerciseLevel.name.lowercase().replace('_', ' ')
                                .replaceFirstChar(Char::uppercase),
                            jobActivityLevel = profile.jobActivityLevel.name.lowercase().replace('_', ' ')
                                .replaceFirstChar(Char::uppercase),
                            goalType = profile.goalType.name.lowercase().replaceFirstChar(Char::uppercase),
                            baselineTarget = "${profile.calorieTarget} kcal/day",
                            workingTarget = current.workingTarget.takeUnless { it == "--" }
                                ?: "${profile.calorieTarget} kcal/day",
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            observeNutritionGuidanceUseCase().collect { guidance ->
                mutableUiState.update { current ->
                    current.copy(
                        workingTarget = guidance?.calorieTarget?.let { "$it kcal/day" } ?: current.baselineTarget,
                        proteinRange = guidance?.suggestedProteinRange,
                        carbRange = guidance?.suggestedCarbRange,
                        fatRange = guidance?.suggestedFatRange,
                        explanation = guidance?.derivationExplanation,
                    )
                }
            }
        }
    }
}

package com.andrewenfusion.alphafitness.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrewenfusion.alphafitness.core.common.error.AppError
import com.andrewenfusion.alphafitness.core.common.result.AppResult
import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex
import com.andrewenfusion.alphafitness.domain.model.UserProfile
import com.andrewenfusion.alphafitness.domain.usecase.ObserveUserProfileUseCase
import com.andrewenfusion.alphafitness.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserProfileUseCase().collect { profile ->
                mutableUiState.update { current ->
                    if (profile == null) {
                        current.copy(
                            isLoading = false,
                            saveSucceeded = false,
                            error = null,
                        )
                    } else {
                        profile.toUiState(
                            isSaving = current.isSaving,
                            saveSucceeded = current.saveSucceeded,
                            error = current.error,
                        )
                    }
                }
            }
        }
    }

    fun onAgeChanged(value: String) {
        updateField { copy(age = value, error = null, saveSucceeded = false) }
    }

    fun onHeightChanged(value: String) {
        updateField { copy(heightCm = value, error = null, saveSucceeded = false) }
    }

    fun onWeightChanged(value: String) {
        updateField { copy(weightKg = value, error = null, saveSucceeded = false) }
    }

    fun onSexSelected(value: Sex) {
        updateField { copy(sex = value, error = null, saveSucceeded = false) }
    }

    fun onExerciseLevelSelected(value: ExerciseLevel) {
        updateField { copy(exerciseLevel = value, error = null, saveSucceeded = false) }
    }

    fun onJobActivityLevelSelected(value: JobActivityLevel) {
        updateField { copy(jobActivityLevel = value, error = null, saveSucceeded = false) }
    }

    fun onGoalTypeSelected(value: GoalType) {
        updateField { copy(goalType = value, error = null, saveSucceeded = false) }
    }

    fun onSaveClicked() {
        val current = uiState.value
        val age = current.age.toIntOrNull()
        val heightCm = current.heightCm.toFloatOrNull()
        val weightKg = current.weightKg.toFloatOrNull()

        if (age == null || heightCm == null || weightKg == null) {
            mutableUiState.update {
                it.copy(
                    error = OnboardingError.InvalidNumericInput,
                    saveSucceeded = false,
                )
            }
            return
        }

        viewModelScope.launch {
            mutableUiState.update { it.copy(isSaving = true, error = null, saveSucceeded = false) }

            when (
                val result = saveUserProfileUseCase(
                    age = age,
                    sex = current.sex,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    exerciseLevel = current.exerciseLevel,
                    jobActivityLevel = current.jobActivityLevel,
                    goalType = current.goalType,
                )
            ) {
                is AppResult.Success -> {
                    mutableUiState.update { it.copy(isSaving = false, saveSucceeded = true) }
                }
                is AppResult.Failure -> {
                    mutableUiState.update {
                        it.copy(
                            isSaving = false,
                            saveSucceeded = false,
                            error = result.error.toOnboardingError(),
                        )
                    }
                }
            }
        }
    }

    private fun updateField(update: OnboardingUiState.() -> OnboardingUiState) {
        mutableUiState.update { current ->
            current.update().copy(isLoading = false)
        }
    }

    private fun UserProfile.toUiState(
        isSaving: Boolean,
        saveSucceeded: Boolean,
        error: OnboardingError?,
    ): OnboardingUiState =
        OnboardingUiState(
            age = age.toString(),
            heightCm = heightCm.toString(),
            weightKg = weightKg.toString(),
            sex = sex,
            exerciseLevel = exerciseLevel,
            jobActivityLevel = jobActivityLevel,
            goalType = goalType,
            isLoading = false,
            isSaving = isSaving,
            saveSucceeded = saveSucceeded,
            error = error,
        )

    private fun AppError.toOnboardingError(): OnboardingError = OnboardingError.Message(value = message)
}

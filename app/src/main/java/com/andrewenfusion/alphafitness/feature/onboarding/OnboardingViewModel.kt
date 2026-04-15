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
import com.andrewenfusion.alphafitness.domain.usecase.ObserveNutritionGuidanceUseCase
import com.andrewenfusion.alphafitness.domain.usecase.ObserveUserProfileUseCase
import com.andrewenfusion.alphafitness.domain.usecase.RefreshNutritionGuidanceUseCase
import com.andrewenfusion.alphafitness.domain.usecase.ResetNutritionGuidanceToBaselineUseCase
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
    observeNutritionGuidanceUseCase: ObserveNutritionGuidanceUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val refreshNutritionGuidanceUseCase: RefreshNutritionGuidanceUseCase,
    private val resetNutritionGuidanceToBaselineUseCase: ResetNutritionGuidanceToBaselineUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = mutableUiState.asStateFlow()
    private var currentProfile: UserProfile? = null

    init {
        viewModelScope.launch {
            observeUserProfileUseCase().collect { profile ->
                currentProfile = profile
                mutableUiState.update { current ->
                    if (profile == null) {
                        current.copy(
                            isLoading = false,
                            saveSucceeded = false,
                            error = null,
                            calorieTarget = null,
                            guidanceCalorieTarget = null,
                            guidanceProteinRange = null,
                            guidanceCarbRange = null,
                            guidanceFatRange = null,
                            guidanceExplanation = null,
                            guidanceNotes = null,
                            guidanceError = null,
                            guidanceStatus = null,
                            isRefreshingGuidance = false,
                            isResettingGuidance = false,
                        )
                    } else {
                        profile.toUiState(
                            isSaving = current.isSaving,
                            isRefreshingGuidance = current.isRefreshingGuidance,
                            isResettingGuidance = current.isResettingGuidance,
                            saveSucceeded = current.saveSucceeded,
                            error = current.error,
                            guidanceCalorieTarget = current.guidanceCalorieTarget,
                            guidanceProteinRange = current.guidanceProteinRange,
                            guidanceCarbRange = current.guidanceCarbRange,
                            guidanceFatRange = current.guidanceFatRange,
                            guidanceExplanation = current.guidanceExplanation,
                            guidanceNotes = current.guidanceNotes,
                            guidanceError = current.guidanceError,
                            guidanceStatus = current.guidanceStatus,
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            observeNutritionGuidanceUseCase().collect { guidance ->
                mutableUiState.update { current ->
                    current.copy(
                        guidanceCalorieTarget = guidance?.calorieTarget,
                        guidanceProteinRange = guidance?.suggestedProteinRange,
                        guidanceCarbRange = guidance?.suggestedCarbRange,
                        guidanceFatRange = guidance?.suggestedFatRange,
                        guidanceExplanation = guidance?.derivationExplanation,
                        guidanceNotes = guidance?.notes,
                        guidanceError = null,
                    )
                }
            }
        }
    }

    fun onAgeChanged(value: String) {
        updateField {
            copy(
                age = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onHeightChanged(value: String) {
        updateField {
            copy(
                heightCm = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onWeightChanged(value: String) {
        updateField {
            copy(
                weightKg = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onSexSelected(value: Sex) {
        updateField {
            copy(
                sex = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onExerciseLevelSelected(value: ExerciseLevel) {
        updateField {
            copy(
                exerciseLevel = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onJobActivityLevelSelected(value: JobActivityLevel) {
        updateField {
            copy(
                jobActivityLevel = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onGoalTypeSelected(value: GoalType) {
        updateField {
            copy(
                goalType = value,
                error = null,
                guidanceError = null,
                guidanceStatus = null,
                saveSucceeded = false,
            )
        }
    }

    fun onResetWorkingTargetClicked() {
        val profile = currentProfile ?: return

        viewModelScope.launch {
            mutableUiState.update {
                it.copy(
                    isResettingGuidance = true,
                    guidanceError = null,
                    guidanceStatus = null,
                )
            }

            when (val result = resetNutritionGuidanceToBaselineUseCase(profile)) {
                is AppResult.Success -> {
                    mutableUiState.update {
                        it.copy(
                            isResettingGuidance = false,
                            guidanceError = null,
                            guidanceStatus = OnboardingGuidanceStatus.ResetToBaseline,
                        )
                    }
                }
                is AppResult.Failure -> {
                    mutableUiState.update {
                        it.copy(
                            isResettingGuidance = false,
                            guidanceError = result.error.message,
                        )
                    }
                }
            }
        }
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
            mutableUiState.update {
                it.copy(
                    isSaving = true,
                    error = null,
                    guidanceError = null,
                    guidanceStatus = null,
                    saveSucceeded = false,
                )
            }

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
                    mutableUiState.update {
                        it.copy(
                            isSaving = false,
                            isRefreshingGuidance = true,
                            saveSucceeded = true,
                        )
                    }

                    when (val guidanceResult = refreshNutritionGuidanceUseCase(result.value)) {
                        is AppResult.Success -> {
                            mutableUiState.update {
                                it.copy(
                                    isRefreshingGuidance = false,
                                    guidanceError = null,
                                    guidanceStatus = if (result.value.calorieTarget == guidanceResult.value.calorieTarget) {
                                        OnboardingGuidanceStatus.MatchesBaseline
                                    } else {
                                        OnboardingGuidanceStatus.AdjustedWorkingTarget
                                    },
                                )
                            }
                        }
                        is AppResult.Failure -> {
                            mutableUiState.update {
                                it.copy(
                                    isRefreshingGuidance = false,
                                    guidanceError = guidanceResult.error.message,
                                )
                            }
                        }
                    }
                }
                is AppResult.Failure -> {
                    mutableUiState.update {
                        it.copy(
                            isSaving = false,
                            isRefreshingGuidance = false,
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
        isRefreshingGuidance: Boolean,
        isResettingGuidance: Boolean,
        saveSucceeded: Boolean,
        error: OnboardingError?,
        guidanceCalorieTarget: Int?,
        guidanceProteinRange: String?,
        guidanceCarbRange: String?,
        guidanceFatRange: String?,
        guidanceExplanation: String?,
        guidanceNotes: String?,
        guidanceError: String?,
        guidanceStatus: OnboardingGuidanceStatus?,
    ): OnboardingUiState =
        OnboardingUiState(
            age = age.toString(),
            heightCm = heightCm.toString(),
            weightKg = weightKg.toString(),
            sex = sex,
            exerciseLevel = exerciseLevel,
            jobActivityLevel = jobActivityLevel,
            goalType = goalType,
            calorieTarget = calorieTarget.takeIf { it > 0 },
            isLoading = false,
            isSaving = isSaving,
            isRefreshingGuidance = isRefreshingGuidance,
            isResettingGuidance = isResettingGuidance,
            saveSucceeded = saveSucceeded,
            error = error,
            guidanceCalorieTarget = guidanceCalorieTarget,
            guidanceProteinRange = guidanceProteinRange,
            guidanceCarbRange = guidanceCarbRange,
            guidanceFatRange = guidanceFatRange,
            guidanceExplanation = guidanceExplanation,
            guidanceNotes = guidanceNotes,
            guidanceError = guidanceError,
            guidanceStatus = guidanceStatus,
        )

    private fun AppError.toOnboardingError(): OnboardingError = OnboardingError.Message(value = message)
}

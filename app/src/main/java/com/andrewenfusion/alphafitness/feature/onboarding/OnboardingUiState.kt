package com.andrewenfusion.alphafitness.feature.onboarding

import com.andrewenfusion.alphafitness.domain.model.ExerciseLevel
import com.andrewenfusion.alphafitness.domain.model.GoalType
import com.andrewenfusion.alphafitness.domain.model.JobActivityLevel
import com.andrewenfusion.alphafitness.domain.model.Sex

data class OnboardingUiState(
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val sex: Sex = Sex.UNSPECIFIED,
    val exerciseLevel: ExerciseLevel = ExerciseLevel.UNSPECIFIED,
    val jobActivityLevel: JobActivityLevel = JobActivityLevel.UNSPECIFIED,
    val goalType: GoalType = GoalType.UNSPECIFIED,
    val calorieTarget: Int? = null,
    val guidanceCalorieTarget: Int? = null,
    val guidanceProteinRange: String? = null,
    val guidanceCarbRange: String? = null,
    val guidanceFatRange: String? = null,
    val guidanceExplanation: String? = null,
    val guidanceNotes: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isRefreshingGuidance: Boolean = false,
    val isResettingGuidance: Boolean = false,
    val saveSucceeded: Boolean = false,
    val error: OnboardingError? = null,
    val guidanceError: String? = null,
    val guidanceStatus: OnboardingGuidanceStatus? = null,
) {
    val hasWorkingTargetOverride: Boolean
        get() = calorieTarget != null &&
            guidanceCalorieTarget != null &&
            calorieTarget != guidanceCalorieTarget

    val canSave: Boolean
        get() = age.isNotBlank() &&
            heightCm.isNotBlank() &&
            weightKg.isNotBlank() &&
            sex != Sex.UNSPECIFIED &&
            exerciseLevel != ExerciseLevel.UNSPECIFIED &&
            jobActivityLevel != JobActivityLevel.UNSPECIFIED &&
            goalType != GoalType.UNSPECIFIED &&
            !isSaving &&
            !isRefreshingGuidance &&
            !isResettingGuidance

    val canResetWorkingTarget: Boolean
        get() = hasWorkingTargetOverride &&
            !isSaving &&
            !isRefreshingGuidance &&
            !isResettingGuidance

    val canRetryGuidance: Boolean
        get() = calorieTarget != null &&
            guidanceError != null &&
            !isSaving &&
            !isRefreshingGuidance &&
            !isResettingGuidance
}

package com.andrewenfusion.alphafitness.core.config

import com.andrewenfusion.alphafitness.domain.model.GoalType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionGuidanceConfig @Inject constructor() {
    fun macroSplit(goalType: GoalType): MacroSplit =
        when (goalType) {
            GoalType.LOSE -> MacroSplit(
                proteinLower = 0.30f,
                proteinUpper = 0.35f,
                carbLower = 0.35f,
                carbUpper = 0.40f,
                fatLower = 0.25f,
                fatUpper = 0.30f,
            )
            GoalType.GAIN -> MacroSplit(
                proteinLower = 0.20f,
                proteinUpper = 0.25f,
                carbLower = 0.45f,
                carbUpper = 0.50f,
                fatLower = 0.25f,
                fatUpper = 0.30f,
            )
            GoalType.MAINTAIN,
            GoalType.UNSPECIFIED,
            -> MacroSplit(
                proteinLower = 0.25f,
                proteinUpper = 0.30f,
                carbLower = 0.40f,
                carbUpper = 0.45f,
                fatLower = 0.25f,
                fatUpper = 0.30f,
            )
        }
}

data class MacroSplit(
    val proteinLower: Float,
    val proteinUpper: Float,
    val carbLower: Float,
    val carbUpper: Float,
    val fatLower: Float,
    val fatUpper: Float,
)

package com.andrewenfusion.alphafitness.domain.repository

import com.andrewenfusion.alphafitness.domain.model.MealEntry
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun observeMeals(): Flow<List<MealEntry>>
}

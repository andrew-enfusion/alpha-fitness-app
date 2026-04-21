package com.andrewenfusion.alphafitness.domain.engine

import com.andrewenfusion.alphafitness.core.config.LocalMealMemoryConfig
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemory
import com.andrewenfusion.alphafitness.domain.model.SavedMealMemoryItem
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LocalMealMemoryMatcherTest {
    private val matcher = LocalMealMemoryMatcher(LocalMealMemoryConfig())

    @Test
    fun reusesOnlyMatchedItemsFromASavedMeal() {
        val result = matcher.match(
            submittedText = "yogurt and granola",
            recentSavedMeals = listOf(
                SavedMealMemory(
                    mealEntryId = "meal-1",
                    rawInput = "Greek yogurt with granola and blueberries",
                    timestamp = Instant.parse("2026-04-20T08:00:00Z"),
                    items = listOf(
                        SavedMealMemoryItem(
                            displayName = "Greek yogurt",
                            portionDescription = "1 serving",
                            calories = 190,
                            protein = 14f,
                            carbs = 12f,
                            fat = 5f,
                            assumptions = "Used the last confirmed serving.",
                            confidence = 0.9f,
                        ),
                        SavedMealMemoryItem(
                            displayName = "Granola",
                            portionDescription = "1 handful",
                            calories = 210,
                            protein = 5f,
                            carbs = 30f,
                            fat = 8f,
                            assumptions = "Used the last confirmed serving.",
                            confidence = 0.88f,
                        ),
                        SavedMealMemoryItem(
                            displayName = "Blueberries",
                            portionDescription = "1 small scoop",
                            calories = 40,
                            protein = 0.5f,
                            carbs = 10f,
                            fat = 0f,
                            assumptions = "Used the last confirmed serving.",
                            confidence = 0.84f,
                        ),
                    ),
                ),
            ),
            now = Instant.parse("2026-04-21T12:00:00Z"),
        )

        assertNotNull(result)
        assertEquals(2, result?.items?.size)
        assertEquals(listOf("Greek yogurt", "Granola"), result?.items?.map { item -> item.displayName })
    }

    @Test
    fun returnsNullWhenNoRecentMealMatchesNaturally() {
        val result = matcher.match(
            submittedText = "beef burrito bowl",
            recentSavedMeals = listOf(
                SavedMealMemory(
                    mealEntryId = "meal-1",
                    rawInput = "Greek yogurt with granola",
                    timestamp = Instant.parse("2026-04-20T08:00:00Z"),
                    items = listOf(
                        SavedMealMemoryItem(
                            displayName = "Greek yogurt",
                            portionDescription = "1 serving",
                            calories = 190,
                            protein = 14f,
                            carbs = 12f,
                            fat = 5f,
                            assumptions = "Used the last confirmed serving.",
                            confidence = 0.9f,
                        ),
                    ),
                ),
            ),
            now = Instant.parse("2026-04-21T12:00:00Z"),
        )

        assertNull(result)
    }
}

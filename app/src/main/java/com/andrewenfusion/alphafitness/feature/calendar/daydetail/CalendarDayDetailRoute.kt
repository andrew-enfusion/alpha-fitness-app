package com.andrewenfusion.alphafitness.feature.calendar.daydetail

import androidx.compose.runtime.Composable

@Composable
fun CalendarDayDetailRoute(
    dateLabel: String,
    onNavigateBack: () -> Unit,
) {
    CalendarDayDetailScreen(
        uiState = CalendarDayDetailUiState(dayLabel = dateLabel),
        onNavigateBack = onNavigateBack,
    )
}

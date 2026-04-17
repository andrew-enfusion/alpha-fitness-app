package com.andrewenfusion.alphafitness.feature.calendar

import androidx.compose.runtime.Composable
import java.time.LocalDate

@Composable
fun CalendarRoute(
    onOpenDayDetail: (String) -> Unit,
) {
    CalendarScreen(
        uiState = CalendarUiState(
            selectedDayLabel = LocalDate.now().toString(),
        ),
        onOpenDayDetail = onOpenDayDetail,
    )
}

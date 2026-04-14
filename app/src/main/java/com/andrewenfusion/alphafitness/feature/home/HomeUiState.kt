package com.andrewenfusion.alphafitness.feature.home

data class HomeUiState(
    val title: String = "Alpha Fitness App",
    val subtitle: String = "Phase 1 architecture scaffold",
    val scaffoldItems: List<String> = listOf(
        "Navigation shell",
        "Hilt dependency injection skeleton",
        "Room database shell",
        "Repository interfaces",
        "Shared result and error wrappers",
        "Theme and design system shell",
    ),
    val roadmapNotice: String = "Phase 2+ feature logic intentionally deferred.",
)

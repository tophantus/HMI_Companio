package com.example.companio.ui.navigation

import com.example.companio.R

interface NavigationDestination {
    val route: String
    val titleRes: Int
}

object DetectionDestination : NavigationDestination {
    override val route = "detection"
    override val titleRes = R.string.detection
}

object MoodTrackingDestination: NavigationDestination {
    override val route = "tracking"
    override val titleRes = R.string.mood_tracking
}

object FaceRecognition: NavigationDestination {
    override val route = "face_recognition"
    override val titleRes = R.string.face_recognition
}
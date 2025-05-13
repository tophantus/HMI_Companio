package com.example.companio.ui.navigation

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.companio.features.object_detection.YuvToRgbConverter
import com.example.companio.ui.views.CameraPermission
import com.example.companio.ui.views.DetectionScreen
import com.example.companio.ui.views.FaceRecognitionScreen
import com.example.companio.ui.views.MoodTrackingScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ApplicationNavHost(
    navController: NavHostController,
    cameraExecutor: ExecutorService,
    yuvToRgbConverter: YuvToRgbConverter,
    interpreter: Interpreter,
    labels: List<String>,
    textToSpeech: TextToSpeech,
    modifier: Modifier = Modifier
) {

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
    } else {
        CameraPermission(cameraPermissionState)
    }

    NavHost(
        navController = navController,
        startDestination = DetectionDestination.route,
        modifier = modifier
    ) {
        composable(route = DetectionDestination.route) {
            DetectionScreen(cameraExecutor = cameraExecutor,
                yuvToRgbConverter = yuvToRgbConverter,
                interpreter = interpreter,
                labels = labels,
                textToSpeech = textToSpeech,
                navigateToSocializingMode = {navController.navigate(MoodTrackingDestination.route)}
            )
        }
        composable(route = MoodTrackingDestination.route) {
            MoodTrackingScreen(cameraExecutor = cameraExecutor,
                navigateToFaceRecognition = {navController.navigate(FaceRecognition.route)},
                navigateToExploreMode = {navController.navigate(DetectionDestination.route)}
            )
        }
        composable(route = FaceRecognition.route) {
            FaceRecognitionScreen(
                cameraExecutor = cameraExecutor,
                navigateToMoodTracking = {navController.navigate(MoodTrackingDestination.route)},
                navigateToExploreMode = {navController.navigate(DetectionDestination.route)}
            )

        }
    }
}
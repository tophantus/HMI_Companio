package com.example.companio.ui.views

import android.annotation.SuppressLint
import android.graphics.PointF
import android.media.MediaPlayer
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.companio.DragThreshold
import com.example.companio.R
import com.example.companio.SocializingModeBar
import com.example.companio.features.face_detection.FaceDetectionAnalyzer
import com.example.companio.presentation.MainViewModel
import com.example.companio.utils.adjustPoint
import com.example.companio.utils.adjustSize
import com.example.companio.utils.drawBounds
import com.google.mlkit.vision.face.Face
import java.util.concurrent.ExecutorService
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MoodTrackingScreen(
    cameraExecutor: ExecutorService,
    moodTrackingViewModel: MainViewModel = hiltViewModel(),
    navigateToFaceRecognition: () -> Unit = {},
    navigateToExploreMode: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView: PreviewView

    val screenWidth = remember { mutableIntStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableIntStateOf(context.resources.displayMetrics.heightPixels) }


    val imageWidth = remember { mutableIntStateOf(0) }
    val imageHeight = remember { mutableIntStateOf(0) }

    val moodTrackSound = remember { MediaPlayer.create(context, R.raw.mood_tracking) }
    val happySound = remember { MediaPlayer.create(context, R.raw.happy_sound) }
    val upsetSound = remember { MediaPlayer.create(context, R.raw.sad_sound) }

    LaunchedEffect(Unit) {
        moodTrackSound.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            moodTrackSound.stop()
            happySound.stop()
            upsetSound.stop()

            moodTrackSound.release()
            happySound.release()
            upsetSound.release()
        }
    }

    val faces = remember { mutableStateListOf<Face>() }

    val mood = remember { mutableStateOf<MoodState>(MoodState.Normal) }

    LaunchedEffect(faces.size) {
        if (faces.isEmpty()) {
            mood.value = MoodState.Normal
        }
    }

    val faceDetectionAnalyzer = FaceDetectionAnalyzer { detectedFace, width, height ->
        faces.clear()
        faces.addAll(detectedFace)
        imageWidth.intValue = width
        imageHeight.intValue = height
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetRotation(android.view.Surface.ROTATION_0)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(cameraExecutor, faceDetectionAnalyzer)
        }

    moodTrackingViewModel.initRepo(imageAnalysis)

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { _, dragAmount ->
                    if (abs(dragAmount.x) > abs(dragAmount.y)) {
                        if (abs(dragAmount.x) > DragThreshold) {
                            navigateToFaceRecognition()
                        }
                    } else {
                        if (abs(dragAmount.y) > DragThreshold) {
                                navigateToExploreMode()
                        }
                    }
                }
            )
        },
        topBar = {
            SocializingModeBar(destinationName = "Mood Tracking")
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    previewView = PreviewView(it)
                    moodTrackingViewModel.showCameraPreview(previewView = previewView, lifecycleOwner = lifecycleOwner)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                when (mood.value) {
                                    is MoodState.Happy -> {
                                        happySound.start()
                                    }
                                    is MoodState.Sad -> {
                                        upsetSound.start()
                                    }
                                    else -> {}
                                }
                            }
                        )
                    }
            )
            DrawFaces(faces, imageHeight.intValue, imageWidth.intValue, screenWidth.intValue, screenHeight.intValue, updateEmotionState = { smile, upset ->
                when {
                    smile > 0.7 -> {
                        mood.value = MoodState.Happy
                    }
                    upset > 0.6 -> {
                        mood.value = MoodState.Sad
                    }
                    else -> {
                        mood.value = MoodState.Normal
                    }
                }
            })
        }
    }
}

@Composable
fun DrawFaces(faces: List<Face>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int, updateEmotionState: (Float, Float) -> Unit) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach { face ->
            val boundingBox = face.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.left, boundingBox.top), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(
                boundingBox.size,
                imageWidth, imageHeight, screenWidth, screenHeight
            )


            val smileLevel = "Smile: ${((face.smilingProbability ?: 0f) * 100).toInt()}%"
            val upsetLevel = "Upset: ${calculateUpsetLevel(face)}%"

            if (((face.smilingProbability ?: 0f) * 100).toInt() > calculateUpsetLevel(face)) {
                drawBounds(topLeft, size, Color.Magenta, 5f)
            } else {
                drawBounds(topLeft, size, Color.Red, 5f)
            }


            updateEmotionState(face.smilingProbability ?: 0f, calculateUpsetLevel(face).toFloat())

            drawContext.canvas.nativeCanvas.drawText(
                smileLevel,
                topLeft.x,
                topLeft.y - 10,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.MAGENTA
                    textSize = 40f
                }
            )

            drawContext.canvas.nativeCanvas.drawText(
                upsetLevel,
                topLeft.x,
                topLeft.y - 60,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                    textSize = 40f
                }
            )
        }
    }
}

fun calculateUpsetLevel(face: Face): Int {
    // Define weights for each attribute
    val smileWeight = 0.5f
    val eyeOpenWeight = 0.3f
    val headPoseWeight = 0.2f

    // Calculate the smile score (higher probability means less upset)
    val smileScore = 1 - (face.smilingProbability ?: 0f)

    // Calculate the eye openness score (lower probability means more upset)
    val leftEyeOpenScore = 1 - (face.leftEyeOpenProbability ?: 0f)
    val rightEyeOpenScore = 1 - (face.rightEyeOpenProbability ?: 0f)
    val eyeOpenScore = (leftEyeOpenScore + rightEyeOpenScore) / 2

    // Calculate the head pose score (larger angles mean more upset)
    val maxAngle = 30.0f
    val normalizedX = abs(face.headEulerAngleX) / maxAngle
    val normalizedY = abs(face.headEulerAngleY) / maxAngle
    val normalizedZ = abs(face.headEulerAngleZ) / maxAngle
    val headPoseScore = (normalizedX + normalizedY + normalizedZ) / 3

    // Combine the scores using the defined weights
    val upsetLevel = (smileScore * smileWeight + eyeOpenScore * eyeOpenWeight + headPoseScore * headPoseWeight) * 100

    return upsetLevel.toInt()
}

sealed class MoodState {
    data object Normal: MoodState()
    data object Happy: MoodState()
    data object Sad: MoodState()
}


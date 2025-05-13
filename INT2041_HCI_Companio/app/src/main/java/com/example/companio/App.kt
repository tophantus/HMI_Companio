package com.example.companio

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.companio.features.object_detection.YuvToRgbConverter
import com.example.companio.ui.navigation.ApplicationNavHost
import com.example.companio.ui.theme.Blue700
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService

@Composable
fun AppBar(
    destinationName: String,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex = 0
    if (destinationName == stringResource(R.string.detection)) {
        selectedTabIndex = 0
    } else if (destinationName == stringResource(R.string.explore)) {
        selectedTabIndex = 1
    }

    val tabs = listOf("Object Recognition")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            val tab = tabPositions[selectedTabIndex]

            Box(
                Modifier
                    .tabIndicatorOffset(tab)
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colors.primary, MaterialTheme.colors.primaryVariant, Blue700)
                        ),
                        shape = RectangleShape
                    )
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { /**/ },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            )
        }
    }
}

@Composable
fun SocializingModeBar(
    destinationName: String,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex = 0
    if (destinationName == stringResource(R.string.mood_tracking)) {
        selectedTabIndex = 0
    } else if (destinationName == stringResource(R.string.face_recognition)) {
        selectedTabIndex = 1
    }

    val tabs = listOf("Mood Analytics", "Identity Detection")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            val tab = tabPositions[selectedTabIndex]

            Box(
                Modifier
                    .tabIndicatorOffset(tab)
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colors.primary, MaterialTheme.colors.primaryVariant, Blue700)
                        ),
                        shape = RectangleShape
                    )
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { /**/ },
                text = {
                    Text(
                        text = title,
                        color = if (selectedTabIndex == index) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            )
        }
    }
}


@Composable
fun App(navHostController: NavHostController = rememberNavController(), cameraExecutor: ExecutorService, yuvToRgbConverter: YuvToRgbConverter, interpreter: Interpreter, labels: List<String>, textToSpeech: TextToSpeech) {
    ApplicationNavHost(navController = navHostController, cameraExecutor = cameraExecutor, yuvToRgbConverter = yuvToRgbConverter, interpreter = interpreter, labels = labels, textToSpeech = textToSpeech)
}
